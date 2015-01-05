package drunkmafia.thaumicinfusion.common.util;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class TileEntityClassTransformer implements ClassFileTransformer {

    private ClassPool pool;

    public TileEntityClassTransformer() {
        pool = ClassPool.getDefault();
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            pool.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));
            CtClass cclass = pool.get(className.replaceAll("/", "."));
            if (!cclass.isFrozen()) {
                for (CtMethod currentMethod : cclass.getDeclaredMethods()) {
                    currentMethod.insertBefore("{ System.out.println(\"Hello\");}");
                    currentMethod.insertAfter("{ System.out.println(\"Goodbyte\");}");
                }
                return cclass.toBytecode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
