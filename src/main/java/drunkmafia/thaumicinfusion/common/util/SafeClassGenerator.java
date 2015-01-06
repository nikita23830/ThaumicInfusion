package drunkmafia.thaumicinfusion.common.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import org.apache.logging.log4j.core.Logger;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class SafeClassGenerator {

    Logger log;
    ClassPool cp;

    static final int[] bannedAcessLevels = {0, 9, 16};
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
        primitiveReturn.put("float", "0");
        primitiveReturn.put("double", "0");
        primitiveReturn.put("string", "");
    }

    public void lowestSuper(CtClass ct){
        lowestSuper = ct;
    }

    public Class generateSafeClass(CtClass orginal) {
        if(orginal == null)
            return null;

        if(orginal.getClassFile().isFinal() || orginal.getClassFile().isInterface())
            throw new ClassFormatError("Class: " + orginal.getName() + " has an incompatible access or is an interface");

        try{
            CtMethod[] allMethods = getMethodsFromSuper(orginal);
            CtClass safe = cp.makeClass(orginal.getName() + "Safe");
            CtClass exception = getCtClass(Exception.class);

            if(safe.isFrozen())
                safe.defrost();

            safe.setSuperclass(orginal);
            safe.addInterface(getCtClass(SafeClass.class));
            for(CtMethod method : allMethods){
                try{
                    CtMethod safeMethod = new CtMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), safe);

                    boolean hasReturn = !safeMethod.getReturnType().toString().contains("void");
                    safeMethod.setBody("{ " + (hasReturn ? " return " : "") + "super." + safeMethod.getName() + "($$); }");

                    String retType = safeMethod.getReturnType().getName(), ret = "(" + retType + ")null";
                    if(primitiveReturn.containsKey(retType))
                        ret = primitiveReturn.get(retType);

                    safeMethod.addCatch("{ drunkmafia.thaumicinfusion.common.block.InfusedBlock.handleError($e, this); " + (hasReturn ? " return " + ret + "; }" : "return; }"), exception);
                    safe.addMethod(safeMethod);
                }catch(Exception e){
                    if(log != null)
                        log.error("Method: " + method.getName() + " \n Class: " + method.getDeclaringClass().getName() + " \n Access Level: " + method.getMethodInfo().getAccessFlags() + "\n Error Message: " + e.getMessage());
                }
            }
            safe.writeFile("Test");
            return safe.toClass();
        }catch(Exception e){
            if(log != null)
                log.error("Failed while registering class");
        }
        return null;
    }

    public CtClass getCtClass(Class c){
        if(cp == null)
            cp = ClassPool.getDefault();
        try{
            return cp.get(c.getName());
        }catch(Exception e){
           if(log != null)
               log.info("Error getting: " + c.getName());
        }
        return null;
    }

    CtMethod[] getMethodsFromSuper(CtClass startSuper){
        ArrayList<CtMethod> methods = new ArrayList<CtMethod>();
        try{
            CtClass currentSuper = startSuper;

            while(currentSuper != lowestSuper){
                for(CtMethod method : currentSuper.getDeclaredMethods()){
                    if(hasMethodInArray(methods, method) || !isMethodCompatible(method))
                        continue;
                    methods.add(method);
                }
                currentSuper = currentSuper.getSuperclass();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return methods.toArray(new CtMethod[methods.size()]);
    }

    CtField[] getFieldsFromSuper(CtClass startSuper){
        ArrayList<CtField> fields = new ArrayList<CtField>();
        try{
            CtClass currentSuper = startSuper;
            while(currentSuper != lowestSuper){
                for(CtField field : currentSuper.getDeclaredFields()){
                    if(fields.contains(field))
                        continue;
                    fields.add(field);
                }
                currentSuper = currentSuper.getSuperclass();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return fields.toArray(new CtField[fields.size()]);
    }

    boolean hasMethodInArray(ArrayList<CtMethod> list, CtMethod method){
        for(CtMethod methList : list) {
            try {
                if (methList.getName().matches(method.getName()) && methList.getReturnType() == method.getReturnType() && methList.getParameterTypes().length == method.getParameterTypes().length)
                    return true;
            }catch (Exception e){}
        }
        return false;
    }

    boolean isMethodCompatible(CtMethod meth){
        for(int level : bannedAcessLevels)
            if (level == meth.getMethodInfo().getAccessFlags())
                return false;
        return true;
    }

    public static interface SafeClass{}
}
