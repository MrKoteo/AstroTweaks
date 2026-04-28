/*
package astrotweaks.tweaks;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;


import javax.annotation.Nullable;

public class PathNodeTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("net.minecraft.block.Block".equals(transformedName)) {
            //System.out.println("[AstroTweaks] Transforming Block class");
            return transformBlockClass(basicClass, name);
        }
        return basicClass;
    }
    
    private byte[] transformBlockClass(byte[] bytes, String className) {
        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(bytes);
            classReader.accept(classNode, 0);
            
            boolean transformed = false;
            
            for (MethodNode method : classNode.methods) {
                // find  getAiPathNodeType (deobf)
                if ("getAiPathNodeType".equals(method.name) || 
                    "func_176646_b".equals(method.name)) { // SRG
                    
                    // check sig wth 4 params
                    if ("(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityLiving;)Lnet/minecraft/pathfinding/PathNodeType;".equals(method.desc) ||
                        "(Lawt;Lamy;Let;Lvq;)Lbeh;".equals(method.desc)) { // Obfuscated
                        
                        //System.out.println("[AstroTweaks] Found target method: " + method.name);
                        
                        method.instructions.clear();
                        
                        InsnList insnList = new InsnList();
                        
                        //  (idxs: 0=this, 1=state, 2=world, 3=pos, 4=entity)
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1)); // IBlockState
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 2)); // IBlockAccess
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 3)); // BlockPos
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 4)); // EntityLiving
                        
                        insnList.add(new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "astrotweaks/tweaks/PathNodeTransformer",
                            "getCustomPathNodeType",
                            "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityLiving;)Lnet/minecraft/pathfinding/PathNodeType;",
                            false
                        ));
                        
                        insnList.add(new InsnNode(Opcodes.ARETURN));
                        
                        method.instructions.insert(insnList);
                        transformed = true;
                        //System.out.println("[AstroTweaks] Method transformed successfully");
                    }
                }
            }
            
            if (!transformed) {
                System.err.println("WARNING: Target method not found!");
                return bytes;
            }
            
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();
            
        } catch (Exception e) {
            System.err.println("ERROR transforming class: " + className);
            e.printStackTrace();
            return bytes;
        }
    }
    
    @Nullable
    public static PathNodeType getCustomPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EntityLiving entity) {
        if (state.getBlock().isBurning(world, pos)) {
            return PathNodeType.DAMAGE_FIRE;
        }
        
        if (state.getBlock() instanceof BlockRailBase) {
            return PathNodeType.WALKABLE;
        }
        
        return null;
    }
}
*/