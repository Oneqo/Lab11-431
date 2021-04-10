import java.lang.reflect.*;
import java.util.Arrays;

public class SkeletonBuilder {
    private String className;

    public String getSkeleton(Class source) throws ClassNotFoundException{
        if(source.isInterface()){
            return getInterfaceSkeleton(source);
        }else{
            return getClassSkeleton(source);
        }
    }

    private String getInterfaceSkeleton(Class source) {
        StringBuilder skeleton = new StringBuilder();

        //Package name
        appendPackageName(source,skeleton);

        //Add type header
        appendClassHeader(source, skeleton, "interface ");
        //Add parent interfaces
        Class[] interfaces = source.getInterfaces();
        appendInterfaces(skeleton, interfaces, " extends ");

        //Open body
        skeleton.append("{\n");

        //Add fields
        Field[] fields = source.getDeclaredFields();
        appendFields(skeleton, fields);
        //Add methods
        Method[] methods = source.getDeclaredMethods();
        appendMethods(skeleton, methods);

        //Close body
        skeleton.append('}');

        return skeleton.toString();
    }

    private String getClassSkeleton(Class source) throws ClassNotFoundException {
        StringBuilder skeleton = new StringBuilder();

        //Package name
        appendPackageName(source, skeleton);

        //Add type header
        appendClassHeader(source, skeleton, "class ");

        //Get superclass & interfaces
        Class superClass = source.getSuperclass();
        Class[] interfaces = source.getInterfaces();
        //Add superclass (if present) | Object is ignored since every class is a subclass of Object.
        if(superClass.getName() != Class.forName("java.lang.Object").getName()){
            skeleton.append(" extends ");
            skeleton.append(parseClassName(superClass.getName()));
        }
        //Add declared interfaces
        appendInterfaces(skeleton, interfaces, " implements ");

        //Open body
        skeleton.append("{\n");

        //Add member fields
        Field[] fields = source.getDeclaredFields();
        appendFields(skeleton, fields);
        //Add constructors
        Constructor[] constructors = source.getDeclaredConstructors();
        apppendConstructors(skeleton, constructors);
        //Add member functions
        Method[] methods = source.getDeclaredMethods();
        appendMethods(skeleton, methods);

        //Close body
        skeleton.append('}');

        return skeleton.toString();
    }

    private void appendPackageName(Class source, StringBuilder skeleton) {
        String packageName = source.getPackageName();
        if(!packageName.isEmpty()){
            skeleton.append("package ");
            skeleton.append(source.getPackageName());
            skeleton.append(";\n\n");
        }
    }

    private void appendClassHeader(Class source, StringBuilder skeleton, String type) {
        className = parseClassName(source.getName());
        String modifiers = Modifier.toString(source.getModifiers());
        skeleton.append(modifiers);
        if (!modifiers.isEmpty())
            skeleton.append(' ');
        skeleton.append(type);
        skeleton.append(className);
    }

    private void appendMethods(StringBuilder skeleton, Method[] methods) {
        if (methods.length > 0) {
            Arrays.sort(methods, (Method m1, Method m2) -> {
                return -1 * m1.toString().compareTo(m2.toString());
            });
            skeleton.append("\\\\Methods:\n");
            for (Method m : methods) {
                skeleton.append("\t" + getModifiers(m));
                skeleton.append(m.getAnnotatedReturnType() + " ");
                skeleton.append(m.getName());
                skeleton.append(getParameterList(m));
                skeleton.append(getExceptionList(m));
                skeleton.append(";\n");
            }
        }
    }

    private void apppendConstructors(StringBuilder skeleton, Constructor[] constructors) {
        if(constructors.length > 0){
            Arrays.sort(constructors, (Constructor c1, Constructor c2) -> {return -1*c1.toString().compareTo(c2.toString());});
            skeleton.append("\\\\Constructors:\n");
            for(Constructor c : constructors){
                skeleton.append("\t"+ getModifiers(c));
                skeleton.append(className);
                skeleton.append(getParameterList(c));
                skeleton.append(getExceptionList(c));
                skeleton.append(";\n");
            }
        }
    }

    private void appendInterfaces(StringBuilder skeleton, Class[] interfaces, String msg) {
        if (interfaces.length > 0) {
            skeleton.append(msg);
            for (Class i : interfaces) {
                skeleton.append(parseClassName(i.getName()));
                skeleton.append(',');
            }
            skeleton.deleteCharAt(skeleton.length() - 1);
        }
    }

    private void appendFields(StringBuilder skeleton, Field[] fields) {
        if (fields.length > 0) {
            Arrays.sort(fields, (Field f1, Field f2) -> {
                return -1 * f1.toString().compareTo(f2.toString());
            });
            skeleton.append("\\\\Fields:\n");
            for (Field f : fields) {
                skeleton.append("\t" + getModifiers(f) + f.getAnnotatedType() + " " + f.getName());
                skeleton.append(";\n");
            }
        }
    }

    private String getExceptionList(Executable obj) {
        StringBuilder result = new StringBuilder();
        Class[] exceptions = obj.getExceptionTypes();
        if(exceptions.length>0){
            result.append(" throws ");
            for(Class e : exceptions){
                result.append(e.getName());
                result.append(',');
            }
            result.deleteCharAt(result.length()-1);
        }
        return result.toString();
    }

    private String parseClassName(String name) {
        if(name.lastIndexOf('.') == -1) //if a class is part of the default package, it doesn't have a prefix
            return name;
        else{
            return name.substring(name.lastIndexOf('.') + 1,name.length());
        }
    }

    private String getModifiers(Member obj){
        StringBuilder result = new StringBuilder();
        int modifiers = obj.getModifiers();
        result.append(Modifier.toString(modifiers));
        if(!result.toString().isEmpty()){
            result.append(' ');
        }
        return result.toString();
    }
    private String getParameterList(Executable obj){
        StringBuilder result = new StringBuilder();
        Class[] parameters = obj.getParameterTypes();
        result.append('(');
        if(parameters.length>0){
            for(int i = 0; i!=parameters.length; i++){
                Class temp = parameters[i];
                    result.append(temp.getSimpleName() + " ");
                    result.append(temp.getSimpleName().charAt(0)+""+(i+1));
                    result.append(',');
            }
            result.deleteCharAt(result.length()-1);//remove last coma
        }
        result.append(")");
        return result.toString();
    }
}
