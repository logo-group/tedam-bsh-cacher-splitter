/*
* Copyright 2014-2019 Logo Business Solutions
* (a.k.a. LOGO YAZILIM SAN. VE TIC. A.S)
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/

package com.lbs.tedam.bsh;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * @author Tarik.Mikyas
 *
 */
public class CacherBshSplitter {

	/**
	 * The variable to be used to print the file procedures separately.
	 */
	static EnumMap<MethodFileNameEnum, String> methods = new EnumMap<MethodFileNameEnum, String>(MethodFileNameEnum.class);

	public static void main(String[] args) throws ParseException, IOException {
		File f = new File("C:\\Projects\\test\\Tools\\ty\\Trunk\\TedamBSH\\src\\main\\java\\com\\lbs\\tedam\\bsh\\CacherBsh.java");
		getMethodLineNumbers(f);
		resetFiles();
		writeToFiles();
	}

	/**
	 * In the @String variable, create and return a new string using the String.replace() routine. Inculde is used to remove comments.
	 * 
	 * @author Tarik.Mikyas
	 * @param body
	 * @return
	 */
	private static String refactorBody(String body) {
		String body1, newBody;
		body1 = body.replace("/* $I(..\\Modular_Scripts\\FunctionalScripts\\GetSnapshot.bsh); */", "$I(..\\Modular_Scripts\\FunctionalScripts\\GetSnapshot.bsh);");
		newBody = body1.replace("/* $I(../Modular_Scripts/FunctionalScripts/GetSnapshot.bsh); */", "$I(../Modular_Scripts/FunctionalScripts/GetSnapshot.bsh);");
		return newBody;
	}

	/**
	 * Static defined @EnumMap creates the files according to the map contents by moving over the methods variable.
	 * 
	 * @author Tarik.Mikyas
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private static void writeToFiles() throws IOException {
		Iterator it = methods.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			MethodFileNameEnum currentOperation = (MethodFileNameEnum) pair.getKey();
			File currentFile = new File(currentOperation.getFileName());
			FileWriter fileWriter = new FileWriter(currentFile, true);
			String emptyString = "";
			if (currentFile.exists()) {
				emptyString = "\n";
			}
			fileWriter.append(emptyString + currentOperation.methodName + pair.getValue().toString());
			fileWriter.close();
		}
	}

	/**
	 *Used to delete all existing procedure files.
	 * 
	 * @author Tarik.Mikyas
	 */
	private static void resetFiles() {
		for (MethodFileNameEnum currentOperation : MethodFileNameEnum.values()) {
			System.out.println(currentOperation.fileName);
			new File(currentOperation.fileName).delete();
		}
	}

	@SuppressWarnings("unchecked")
	private static void getMethodLineNumbers(File src) throws IOException, ParseException {
		CompilationUnit cu = JavaParser.parse(src);
		new MethodVisitor().visit(cu, null);
	}

	/**
	 * Simple visitor implementation for visiting MethodDeclaration nodes.
	 */
	@SuppressWarnings("rawtypes")
	private static class MethodVisitor extends VoidVisitorAdapter {
		@Override
		public void visit(MethodDeclaration m, Object arg) {
			String methodName = getMethodName(m);
			methods.put(MethodFileNameEnum.getFileName(methodName), refactorBody(m.getBody().toString()));
		}

		/**
		 * @MethodDeclaration takes an object of the type and returns the full name of that object as @String.
		 * 
		 * @author Tarik.Mikyas
		 * @since 14 Oca 2016 15:52:32
		 * @param m
		 * @return
		 */
		public String getMethodName(MethodDeclaration m) {
			return "public " + m.getType() + " " + m.getName() + "(" + listToString(m.getParameters()) + ")";
		}

		/**
		 * Returns the @Parameter type list given as @String, with commas as parameters to the procedure.
		 * 
		 * @author Tarik.Mikyas
		 * @since 14 Oca 2016 15:46:54
		 * @param list
		 * @return
		 */
		public String listToString(List<Parameter> list) {
			if (list == null) {
				return "";
			}
			String parameters = "";
			for (Parameter parameter : list) {
				parameters += parameter.toString() + ", ";
			}
			parameters = parameters.substring(0, parameters.length() - 2);
			return parameters;
		}
	}

	/**
	 * @EnumMap The static part of the defined methods variable, which will create the key part, the first part is the name of the procedure, the second part is the @Enum of the file path to write the procedure.
	 * 
	 * 
	 * @author Tarik.Mikyas
	 * 
	 */
	enum MethodFileNameEnum {

		CACHER( //
				"public void cacherNG()", //
				"c:\\Projects\\test\\Tools\\ty\\Beanshell_Scripts\\SnapshotCollector\\CacherNG.bsh");//

		private String methodName;
		private String fileName;

		MethodFileNameEnum(String methodName, String fileName) {
			this.methodName = methodName;
			this.fileName = fileName;
		}

		public String getMethodName() {
			return methodName;
		}

		public String getFileName() {
			return fileName;
		}

		/**
		 * The given @String variable returns an enum in the @MethodFileNameEnum type that corresponds to the defined procedure name, depending on the variable.
		 * 
		 * @author Tarik.Mikyas
		 * @param methodName
		 * @return
		 */
		static MethodFileNameEnum getFileName(String methodName) {
			for (MethodFileNameEnum current : MethodFileNameEnum.values()) {
				if (current.getMethodName().equals(methodName)) {
					return current;
				}
			}
			return null;
		}
	}
}