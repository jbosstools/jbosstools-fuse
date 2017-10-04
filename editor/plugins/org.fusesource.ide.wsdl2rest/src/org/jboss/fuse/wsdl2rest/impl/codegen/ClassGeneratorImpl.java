package org.jboss.fuse.wsdl2rest.impl.codegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import org.jboss.fuse.wsdl2rest.ClassGenerator;
import org.jboss.fuse.wsdl2rest.EndpointInfo;
import org.jboss.fuse.wsdl2rest.MethodInfo;
import org.jboss.fuse.wsdl2rest.ParamInfo;
import org.jboss.fuse.wsdl2rest.impl.writer.MessageWriter;
import org.jboss.fuse.wsdl2rest.impl.writer.MessageWriterFactory;

public class ClassGeneratorImpl implements ClassGenerator {

    protected MessageWriter msgWriter = MessageWriterFactory.getMessageWriter();

    protected Path outpath;

    public ClassGeneratorImpl(Path outpath) {
        this.outpath = outpath;
    }

    @Override
    public void generateClasses(List<EndpointInfo> clazzDefs) throws IOException {
        for (EndpointInfo clazzDef : clazzDefs) {
            String packageName = clazzDef.getPackageName();
            packageName = packageName.replace('.', File.separatorChar);
            File packageDir = outpath.resolve(packageName).toFile();
            packageDir.mkdirs();

            File clazzFile = new File(packageDir, clazzDef.getClassName() + ".java");
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(clazzFile)))) {
                writePackageName(writer, clazzDef);
                writeImports(writer, clazzDef);
                writeServiceClass(writer, clazzDef);
            }
        }
    }

    protected void writePackageName(PrintWriter writer, EndpointInfo clazzDef) {
        final String packName = clazzDef.getPackageName();
        if (packName != null && packName.length() != 0) {
            writer.println("package " + packName + ";");
        }
        writer.println();
    }

    protected void writeImports(PrintWriter writer, EndpointInfo clazzDef) {
        if (clazzDef.getImports() != null) {
            for (String impo : clazzDef.getImports()) {
                writer.println("import " + impo + ";");
            }
        }
        writer.println();
    }

    protected void writeServiceClass(PrintWriter writer, EndpointInfo clazzDef) {
        if (clazzDef.getClassName() != null) {
            writer.println("public interface " + clazzDef.getClassName() + " {\n");
            writeMethods(writer, clazzDef.getMethods());
            writer.println("}");
            writer.println();
        }
    }

    protected void writeMethods(PrintWriter writer, List<? extends MethodInfo> methods) {
        if (methods != null) {
            for (MethodInfo minfo : methods) {
                String retType = minfo.getReturnType();
                writer.print("\tpublic " + (retType != null ? retType : "void") + " ");
                writer.print(minfo.getMethodName() + "(");
                writeParams(writer, minfo);
                String excep = minfo.getExceptionType() != null ? (" throws " + minfo.getExceptionType()) : "";
                writer.println(")" + excep + ";");
                writer.println();
            }
        }
    }

    protected void writeMethod(PrintWriter writer, MethodInfo minfo) {
        if (minfo != null) {
            String retType = minfo.getReturnType();
            writer.print("\tpublic " + (retType != null ? retType : "void") + " ");
            writer.print(minfo.getMethodName() + "(");
            writeParams(writer, minfo);
            String excep = minfo.getExceptionType() != null ? (" throws " + minfo.getExceptionType()) : "";
            writer.println(")" + excep + ";");
            writer.println();
        }
    }

    protected void writeParams(PrintWriter writer, MethodInfo minfo) {
        List<ParamInfo> params = minfo.getParams();
        int i = 0;
        for (ParamInfo p : params) {
            writer.print(i++ == 0 ? "" : ", ");
            writer.print(p.getParamType() + " " + p.getParamName());
        }
    }
}
