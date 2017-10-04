package org.jboss.fuse.wsdl2rest.impl.codegen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.fuse.wsdl2rest.EndpointInfo;
import org.jboss.fuse.wsdl2rest.MethodInfo;
import org.jboss.fuse.wsdl2rest.ParamInfo;

//import com.github.javaparser.JavaParser;
//import com.github.javaparser.ParseException;
//import com.github.javaparser.ast.CompilationUnit;
//import com.github.javaparser.ast.body.MethodDeclaration;
//import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class JSR311ClassGenerator extends ClassGeneratorImpl {

    public JSR311ClassGenerator(Path outpath) {
        super(outpath);
    }

    @Override
    protected void writeImports(PrintWriter writer, EndpointInfo clazzDef) {
        writer.println("import javax.ws.rs.Consumes;");
        writer.println("import javax.ws.rs.Produces;");
        writer.println("import javax.ws.rs.DELETE;");
        writer.println("import javax.ws.rs.GET;");
        writer.println("import javax.ws.rs.POST;");
        writer.println("import javax.ws.rs.PUT;");
        writer.println("import javax.ws.rs.Path;");
        writer.println("import javax.ws.rs.PathParam;");
        writer.println("import javax.ws.rs.core.MediaType;");
        super.writeImports(writer, clazzDef);
    }

    @Override
    protected void writeServiceClass(PrintWriter writer, EndpointInfo clazzDef) {
        String pathName = clazzDef.getClassName().toLowerCase();
        writer.println("@Path(\"/" + pathName + "/\")");
        super.writeServiceClass(writer, clazzDef);
    }

    @Override
    protected void writeMethods(PrintWriter writer, List<? extends MethodInfo> methods) {
        for (MethodInfo minfo : methods) {
            String path = minfo.getPath();
            if (path != null) {
                String httpMethod = minfo.getHttpMethod();
                writer.println("\t@" + httpMethod);
                writer.print("\t@Path(\"" + path);

                // Add path param
                if (minfo.getParams().size() > 0) {
                    ParamInfo pinfo = minfo.getParams().get(0);
                    if (hasPathParam(minfo, pinfo)) {
                        writer.print("/{" + pinfo.getParamName() + "}");
                    }
                }
                writer.println("\")");

                // Add @Consumes for PUT,POST 
                if (httpMethod.equals("PUT") || httpMethod.equals("POST")) {
                    writer.println("\t@Consumes(MediaType.APPLICATION_JSON)");
                }

                // Add @Produces for all methods 
                writer.println("\t@Produces(MediaType.APPLICATION_JSON)");
            }
            writeMethod(writer, minfo);
        }
    }

    @Override
    protected void writeParams(PrintWriter writer, MethodInfo minfo) {
        for (int i = 0; i < minfo.getParams().size(); i++) {
            ParamInfo pinfo = minfo.getParams().get(i);
            String name = pinfo.getParamName();
            String type = pinfo.getParamType();
            if (i == 0 && hasPathParam(minfo, pinfo)) {
                writer.print("@PathParam(\"" + name + "\") ");
                writer.print(getNestedParameterType(pinfo) + " " + name);
            } else if (getNestedParameterType(pinfo) != null) {
                writer.print(i == 0 ? "" : ", ");
                writer.print(type + " " + name);
            }
        }
    }

    private boolean hasPathParam(MethodInfo minfo, ParamInfo pinfo) {
        String httpMethod = minfo.getHttpMethod();
        boolean pathParam = httpMethod.equals("GET") || httpMethod.equals("DELETE");
        return pathParam && getNestedParameterType(pinfo) != null;
    }

    private String getNestedParameterType(ParamInfo pinfo) {
        String javaType = pinfo.getParamType();
        File javaFile = outpath.resolve(javaType.replace('.', '/') + ".java").toFile();
        if (javaFile.exists()) {
        	try {
				String javaFileString = readFileToString(javaFile);
				javaType = parse(javaFileString);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//            try (InputStream in = new FileInputStream(javaFile)) {
//                final StringBuffer result = new StringBuffer();
//                CompilationUnit cu = JavaParser.parse(in);
//                new VoidVisitorAdapter<Object>() {
//                    @Override
//                    public void visit(MethodDeclaration decl, Object obj) {
//                        if (result.length() == 0 && decl.getName().startsWith("get")) {
//                            result.append(decl.getType().toStringWithoutComments());
//                        }
//                        super.visit(decl, obj);
//                    }
//                }.visit(cu, null);
//                javaType = result.length() > 0 ? result.toString() : null;
//            } catch (ParseException | IOException ex) {
//                throw new IllegalStateException(ex);
//            }
        }
        return javaType;
    }
    
    
   	//use ASTParse to parse string
   	private String parse(String str) {
        String javaType;
   		ASTParser parser = ASTParser.newParser(AST.JLS3);
   		parser.setSource(str.toCharArray());
   		parser.setKind(ASTParser.K_COMPILATION_UNIT);
    
   		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
    
        final StringBuffer result = new StringBuffer();
   		cu.accept(new ASTVisitor() { 
   			public boolean visit(MethodDeclaration node){
   				if (node.getName().toString().startsWith("get")) {
   					result.append(node.getReturnType2().toString());
   				}
   				return true;
   			}
   		});   		
   		javaType = result.length() > 0 ? result.toString() : null;
   		return javaType;
   	}
    
    //read file content into a string
	private String readFileToString(File file) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));
 
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			System.out.println(numRead);
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
 
		reader.close();
 
		return  fileData.toString();	
	}    
}
