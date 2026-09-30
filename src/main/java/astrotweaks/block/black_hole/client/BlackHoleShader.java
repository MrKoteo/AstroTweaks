package astrotweaks.block.black_hole.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.HashMap;
import java.util.Map;



public class BlackHoleShader {
    private final int programId;
    private final Map<String, Integer> uniformCache = new HashMap<>();

    // Cached locations for the hot-path uniforms (no HashMap lookup per call).
    private int locUHorizon = -2;
    private int locUGravityRange = -2;
    private int locUMass = -2;
    private int locUMode = -2;

    private BlackHoleShader(int program) { this.programId = program; }

    public static BlackHoleShader create(String vertSrc, String fragSrc) throws Exception {
        int vert = compile(GL20.GL_VERTEX_SHADER, vertSrc);
        int frag = compile(GL20.GL_FRAGMENT_SHADER, fragSrc);
        int prog = GL20.glCreateProgram();
        GL20.glAttachShader(prog, vert);
        GL20.glAttachShader(prog, frag);
        GL20.glLinkProgram(prog);
        if (GL20.glGetProgrami(prog, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(prog, 4096);
            throw new Exception("Link failed: " + log);
        }
        if (vert != 0) GL20.glDeleteShader(vert);
        if (frag != 0) GL20.glDeleteShader(frag);
        return new BlackHoleShader(prog);
    }

    private static int compile(int type, String src) throws Exception {
        int id = GL20.glCreateShader(type);
        if (id == 0) throw new Exception("glCreateShader returned 0");
        GL20.glShaderSource(id, src);
        GL20.glCompileShader(id);
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(id, 4096);
            GL20.glDeleteShader(id);
            throw new Exception("Compile failed: " + log);
        }
        return id;
    }

    public void use() { GL20.glUseProgram(programId); }
    public static void stop() { GL20.glUseProgram(0); }

    private int loc(String name) {
        Integer cached = uniformCache.get(name);
        if (cached != null) return cached;
        int l = GL20.glGetUniformLocation(programId, name);
        uniformCache.put(name, l);
        return l;
    }

    public void setFloat(String name, float v) {
        int l = loc(name);
        if (l >= 0) GL20.glUniform1f(l, v);
    }

    public void setVec3(String name, float x, float y, float z) {
        int l = loc(name);
        if (l >= 0) GL20.glUniform3f(l, x, y, z);
    }

    private static void setUniform(int loc, float v) {
        if (loc >= 0) GL20.glUniform1f(loc, v);
    }

    private int locDirect(int cached, String name) {
        if (cached != -2) return cached;
        return loc(name);
    }

    public void setHorizon(float v) {
        locUHorizon = locDirect(locUHorizon, "uHorizon");
        setUniform(locUHorizon, v);
    }

    public void setGravityRange(float v) {
        locUGravityRange = locDirect(locUGravityRange, "uGravityRange");
        setUniform(locUGravityRange, v);
    }

    public void setMass(float v) {
        locUMass = locDirect(locUMass, "uMass");
        setUniform(locUMass, v);
    }

    public void setMode(float v) {
        locUMode = locDirect(locUMode, "uMode");
        setUniform(locUMode, v);
    }

    public void delete() {
        if (programId != 0) GL20.glDeleteProgram(programId);
    }

    // Single source of the shader code (was duplicated with resources/shaders files).
    // Static version: no uTime animation, halos don't rotate/shimmer.
    public static BlackHoleShader loadOrCreate() {
        try {
            return create(EMBEDDED_VERT, EMBEDDED_FRAG);
        } catch (Exception e) {
            System.err.println("[BlackHoleShader] failed to compile: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Shaders - GLSL 120 compatible
    // NOTE: normal MUST be transformed to view space via gl_NormalMatrix.
    // Comparing object-space normal with view-space viewDir was the
    // cause of the North=black / South=brown direction-dependent bug.
    public static final String EMBEDDED_VERT =
            "#version 120\n" +
            "varying vec3 vView;\n" +
            "varying vec3 vNormal;\n" +
            "void main(){\n" +
            "  vec4 viewPos = gl_ModelViewMatrix * gl_Vertex; \n" +
            "  vView = viewPos.xyz; \n" +
            "  vec3 nObj = normalize(gl_Vertex.xyz + vec3(0.0001, 0.0, 0.0));\n" +
            "  vNormal = normalize(gl_NormalMatrix * nObj); \n" +
            "  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex; \n" +
            "}\n";

    public static final String EMBEDDED_FRAG =
            "#version 120\n" +
            "varying vec3 vView;\n" +
            "varying vec3 vNormal;\n" +
            "uniform float uHorizon;\n" +
            "uniform float uGravityRange;\n" +
            "uniform float uMass;\n" +
            "uniform float uMode;\n" +
            "void main(){\n" +
            "  vec3 n = normalize(vNormal);\n" +
            "  vec3 viewDir = normalize(-vView + vec3(0.0001, 0.0, 0.0));\n" +
            "  float ndv = max(0.0, dot(n, viewDir));\n" +
            "  float fresnel = pow(1.0 - ndv, 2.5);\n" +
            "  if (uMode < 0.5) {\n" +
            "    gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);\n" +
            "    return;\n" +
            "  } else if (uMode < 1.5) {\n" +
            "    float alpha = pow(fresnel, 2.0);\n" +
            "    alpha *= 0.44;\n" +
            "    if (alpha < 0.003) discard;\n" +
            "    gl_FragColor = vec4(0.0, 0.0, 0.0, alpha);\n" +
            "    return;\n" +
            "  } else if (uMode < 2.5) {\n" +
            "    float alpha = pow(fresnel, 2.0);\n" +
            "    alpha *= 0.24;\n" +
            "    if (alpha < 0.002) discard;\n" +
            "    gl_FragColor = vec4(0.0, 0.0, 0.0, alpha);\n" +
            "    return;\n" +
            "  } else {\n" +
            "    float alpha = pow(fresnel, 1.5);\n" +
            "    alpha *= 0.17;\n" +
            "    if (alpha < 0.0005) discard;\n" +
            "    gl_FragColor = vec4(0.0, 0.0, 0.0, alpha);\n" +
            "    return;\n" +
            "  }\n" +
            "}\n";
}
