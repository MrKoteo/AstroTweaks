package astrotweaks.block.black_hole.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;



public class BlackHoleShader {
    private final int programId;
    private final Map<String, Integer> uniformCache = new HashMap<>();

    // Cached locations for the hot-path uniforms (no HashMap lookup per call).
    private int locUTime = -2;
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

    public void setTime(float v) {
        locUTime = locDirect(locUTime, "uTime");
        setUniform(locUTime, v);
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

    // Load from resource or fallback to embedded strings
    public static BlackHoleShader loadOrCreate() {
        String vert = null, frag = null;
        try {
            ResourceLocation vLoc = new ResourceLocation("astrotweaks", "shaders/black_hole.vert");
            ResourceLocation fLoc = new ResourceLocation("astrotweaks", "shaders/black_hole.frag");
            InputStream vs = Minecraft.getMinecraft().getResourceManager().getResource(vLoc).getInputStream();
            InputStream fs = Minecraft.getMinecraft().getResourceManager().getResource(fLoc).getInputStream();
            vert = read(vs);
            frag = read(fs);
        } catch (Exception ignored) {}
        if (vert == null) vert = EMBEDDED_VERT;
        if (frag == null) frag = EMBEDDED_FRAG;
        try {
            return create(vert, frag);
        } catch (Exception e) {
            System.err.println("[BlackHoleShader] failed to compile: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static String read(InputStream is) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line).append("\n");
        return sb.toString();
    }

    // Embedded fallback_shaders - GLSL 120 compatible
    // NOTE: normal MUST be transformed to view space via gl_NormalMatrix.
    // Comparing object-space normalize(vPos) with view-space viewDir was the
    // cause of the North=black / South=brown direction-dependent bug.
    public static final String EMBEDDED_VERT =
            "#version 120\n" +
            "varying vec3 vPos;\n" +
            "varying vec3 vView;\n" +
            "varying vec3 vNormal;\n" +
            "void main(){\n" +
            "  vPos = gl_Vertex.xyz; \n" +
            "  vec4 viewPos = gl_ModelViewMatrix * gl_Vertex; \n" +
            "  vView = viewPos.xyz; \n" +
            "  vec3 nObj = normalize(gl_Vertex.xyz + vec3(0.0001, 0.0, 0.0));\n" +
            "  vNormal = normalize(gl_NormalMatrix * nObj); \n" +
            "  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex; \n" +
            "}\n";

    public static final String EMBEDDED_FRAG =
            "#version 120\n" +
            "varying vec3 vPos;\n" +
            "varying vec3 vView;\n" +
            "varying vec3 vNormal;\n" +
            "uniform float uTime;\n" +
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
            "    float shimmer = 0.9 + 0.1 * sin(uTime * 1.1);\n" +
            "    alpha *= 0.44 * shimmer;\n" +
            "    if (alpha < 0.003) discard;\n" +
            "    gl_FragColor = vec4(0.0, 0.0, 0.0, alpha);\n" +
            "    return;\n" +
            "  } else if (uMode < 2.5) {\n" +
            "    float alpha = pow(fresnel, 2.0);\n" +
            "    float ang = atan(vPos.z, vPos.x);\n" +
            "    float shimmer = 0.9 + 0.1 * sin(uTime * 1.1 + ang * 2.0 + 1.5);\n" +
            "    alpha *= 0.24 * shimmer;\n" +
            "    if (alpha < 0.002) discard;\n" +
            "    gl_FragColor = vec4(0.0, 0.0, 0.0, alpha);\n" +
            "    return;\n" +
            "  } else {\n" +
            "    float alpha = pow(fresnel, 1.5);\n" +
            "    float ang = atan(vPos.z, vPos.x);\n" +
            "    float shimmer = 0.9 + 0.1 * sin(uTime * 1.1 + ang * 2.0 + 2.5);\n" +
            "    alpha *= 0.17 * shimmer;\n" +
            "    if (alpha < 0.0005) discard;\n" +
            "    gl_FragColor = vec4(0.0, 0.0, 0.0, alpha);\n" +
            "    return;\n" +
            "  }\n" +
            "}\n";
}
