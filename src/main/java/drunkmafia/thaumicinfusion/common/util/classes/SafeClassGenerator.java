package drunkmafia.thaumicinfusion.common.util.classes;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Loader;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import javassist.*;
import javassist.bytecode.AccessFlag;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class SafeClassGenerator {

    Logger log;
    ClassPool cp;

    static final HashMap<String, String> primitiveReturn = new HashMap<String, String>();

    CtClass lowestSuper;

    public SafeClassGenerator(){
        cp = ClassPool.getDefault();
    }

    static{
        primitiveReturn.put("boolean", "false");
        primitiveReturn.put("byte", "0");
        primitiveReturn.put("short", "0");
        primitiveReturn.put("int", "0");
        primitiveReturn.put("float", "0F");
        primitiveReturn.put("double", "0.0D");
        primitiveReturn.put("string", "");
    }

    public void lowestSuper(CtClass ct){
        lowestSuper = ct;
    }

    public void setLog(Logger log){
        this.log = log;
    }

    public Class generateSafeClass(CtClass orginal) {
        if(orginal == null)
            return null;

        if(orginal.getClassFile().isFinal() || orginal.getClassFile().isInterface())
            throw new ClassFormatError("Class: " + orginal.getName() + " has an incompatible access or is an interface");

        try{
            CtMethod[] allMethods = getMethodsFromSuper(orginal);

            CtClass safe = cp.makeClass(orginal.getName() + "Safe", orginal);
            CtClass exception = getCtClass(Exception.class);

            if(safe.isFrozen())
                safe.defrost();
            safe.stopPruning(true);
            safe.addInterface(getCtClass(SafeClass.class));

            for(CtMethod method : allMethods){
                try{
                    CtMethod safeMethod = new CtMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), safe);
                    safeMethod.getMethodInfo().setAccessFlags(method.getMethodInfo().getAccessFlags());
                    boolean hasReturn = !safeMethod.getReturnType().getName().equals("void");
                    safeMethod.setBody("{ " + (hasReturn ? " return " : "") + "super." + safeMethod.getName() + "($$); }");

                    String retType = safeMethod.getReturnType().getName(), ret;

                    if (primitiveReturn.containsKey(retType))
                        ret = primitiveReturn.get(retType);
                    else
                        ret = "(" + retType + ")null";

                    safeMethod.addCatch("{ drunkmafia.thaumicinfusion.common.block.InfusedBlock.handleError($e, this); " + (hasReturn ? " return " + ret + "; }" : "return; }"), exception);
                    safe.addMethod(safeMethod);
                }catch(Exception e){
                    if(log != null)
                        log.error("Method: " + method.getName() + " \n Class: " + method.getDeclaringClass().getName() + " \n Access Level: " + method.getMethodInfo().getAccessFlags() + "\n Error Message: " + e.getMessage());
                }
            }

            return safe.toClass();
        }catch(Exception e){
            if(log != null)
                log.error("Failed while registering class");
        }
        return null;
    }

    public CtClass getCtClass(Class c){
        try{
            cp.appendClassPath(new javassist.LoaderClassPath(c.getClassLoader()));
            return cp.get(c.getName());
        }catch (Exception e){
            if(log != null)
                log.error("Error getting: " + c.getName(), e);
        }
        return null;
    }

    CtMethod[] getMethodsFromSuper(CtClass startSuper){
        ArrayList<CtMethod> methodsBlack = new ArrayList<CtMethod>();
        ArrayList<CtMethod> methods = new ArrayList<CtMethod>();
        try{
            CtClass currentSuper = startSuper;

            while(currentSuper != lowestSuper){
                for(CtMethod method : currentSuper.getDeclaredMethods()){
                    if(getMethodInArray(methodsBlack, method) != null)
                        continue;

                    if(getMethodInArray(methods, method) != null || !isMethodCompatible(method) || method.isEmpty()) {
                        methodsBlack.add(method);
                        continue;
                    }
                    methods.add(method);
                }
                currentSuper = currentSuper.getSuperclass();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return methods.toArray(new CtMethod[methods.size()]);
    }

    CtMethod getMethodInArray(ArrayList<CtMethod> list, CtMethod method){
        for(CtMethod methList : list) {
            try {
                if (methList.getName().matches(method.getName()) && methList.getReturnType() == method.getReturnType() && methList.getParameterTypes().length == method.getParameterTypes().length)
                    return methList;
            }catch (Exception e){}
        }
        return null;
    }

    boolean isMethodCompatible(CtMethod meth){
        String methCheck = meth.toString();
        return (methCheck.contains("public") || methCheck.contains("protected")) && !methCheck.contains("final") && !methCheck.contains("static") && !methCheck.contains("abstract");
    }

    public static interface SafeClass{}
}
