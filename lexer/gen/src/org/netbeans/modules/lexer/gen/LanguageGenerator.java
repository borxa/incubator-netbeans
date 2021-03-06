/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.netbeans.modules.lexer.gen;

import java.lang.reflect.Field;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;

import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.util.LexerUtilities;
import java.util.Iterator;
import org.netbeans.modules.lexer.gen.util.LexerGenUtilities;

/**
 * Generates Language class source by using xml file
 * with the language description. Descendants of this class
 * typically read some portion of the information
 * (e.g. integer token identifications) from the classes
 * generated by particular lexer.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public class LanguageGenerator {
    
    /** Map containing collected imports */
    private Map imports;

    public LanguageGenerator() {
        this.imports = new HashMap();
    }

    public String createSource(LanguageData data) {
        // Check consistency of language data
        data.check();

        StringBuffer sb = new StringBuffer();
        
        // Class header
        appendClassDeclaration(sb, data);
        
        // Class start
        appendClassStart(sb, data);

        addToImports("org.netbeans.api.lexer.TokenId");

        // TokenId intIds declarations
        for (Iterator idsIterator = data.getSortedIds(
            LanguageData.IDS_INT_ID_COMPARATOR).iterator();
            idsIterator.hasNext();
        ) {
            MutableTokenId id = (MutableTokenId)idsIterator.next();
            LexerGenUtilities.appendSpaces(sb, 4);
            sb.append("public static final int ");
            sb.append(id.getIntIdFieldName());
            sb.append(" = ");
            sb.append(id.getIntId());
            sb.append(";\n");
        }
        
        sb.append("\n\n");
        
        
        for (Iterator idsIterator = data.getSortedIds(
            LanguageData.IDS_FIELD_NAME_COMPARATOR).iterator();
            idsIterator.hasNext();
        ) {
            MutableTokenId id = (MutableTokenId)idsIterator.next();
            LexerGenUtilities.appendSpaces(sb, 4);
            sb.append("public static final TokenId ");
            sb.append(id.getFieldName());
            sb.append(" = new TokenId(\"");
            LexerUtilities.appendToSource(sb, id.getName());
            sb.append("\", ");
            sb.append(id.getIntIdFieldName());
         
            // Append categories
            List catList = id.getCategoryNames();
            int catCnt = catList.size();
            if (catCnt > 0) {
                sb.append(", new String[]{\"");
                for (int j = 0; j < catCnt; j++) {
                    if (j > 0) {
                        sb.append(", \"");
                    }
                    LexerUtilities.appendToSource(sb, (String)catList.get(j));
                    sb.append('"');
                }
                sb.append('}');
            }
            
            // Append texts
            List sampleTexts = id.getSampleTexts();
            int sampleTextsLength = sampleTexts.size();
            if (sampleTextsLength > 0) {
                if (catCnt == 0) { // need to appen null instead of cats
                    sb.append(", null");
                }

                addToImports("org.netbeans.spi.lexer.MatcherFactory");
                sb.append(", MatcherFactory.create");
                String sampleTextCheck = id.getSampleTextCheck();
                if (sampleTextsLength == 1) { // exactly one sample text
                    if ("none".equals(sampleTextCheck)) {
                        sb.append("NoCheckMatcher");
                    } else if ("length".equals(sampleTextCheck)) {
                        sb.append("LengthCheckMatcher");
                    } else {
                        sb.append("TextCheckMatcher");
                    }
                    
                    sb.append("(\"");
                    LexerUtilities.appendToSource(sb, (String)sampleTexts.get(0));
                    sb.append('"');
                    
                } else { // more than one sample text
                    sb.append("MultiMatcher(new String[] {");
                    
                    for (Iterator it = sampleTexts.iterator(); it.hasNext();) {
                        sb.append('"');
                        LexerUtilities.appendToSource(sb, (String)it.next());
                        sb.append('"');
                        if (it.hasNext()) {
                            sb.append(", ");
                        }
                    }
                    sb.append("}");
                }
                sb.append(")");

            }
            
            sb.append(");");
            
            String comment = id.getComment();
            if (comment != null) {
                sb.append(" // ");
                sb.append(comment);
            }
            
            sb.append('\n');
        }
        
        // Constructor
        sb.append('\n');
        appendClassConstructor(sb, data);
        
        // Lexer creation
        LexerGenUtilities.appendSpaces(sb, 4);
        sb.append("public ");
        sb.append(addToImports("org.netbeans.api.lexer.Lexer"));
        sb.append(" createLexer() {\n");
        
        appendLexerMethodBody(sb, data);
        LexerGenUtilities.appendSpaces(sb, 4);
        sb.append("}\n");
        
        // Class end
        appendClassEnd(sb, data);
        
        sb.append("\n}\n");


        // Now header containing package stmt and imports is generated
        StringBuffer hsb = new StringBuffer();
        // [PENDING] ? License
        // Possible package stmt
        String langClassPkgName = getLanguageClassPkgName(data);
        if (!"".equals(langClassPkgName)) {
            hsb.append("\npackage ").append(langClassPkgName).append(";\n");
        }
            
        // Add imports
        hsb.append('\n');
        appendImports(hsb, data);
        hsb.append('\n');

        // Insert package and imports to begining of the generated class
        sb.insert(0, hsb.toString());

        return sb.toString();
    }
    
    private String getLanguageClassNameWithoutPkg(LanguageData data) {
        return addToImports(data.getLanguageClassName());
    }

    private String getLanguageClassPkgName(LanguageData data) {
        return LexerGenUtilities.splitClassName(data.getLanguageClassName())[0];
    }

    protected void appendClassDeclaration(StringBuffer sb, LanguageData data) {
        sb.append("public class ");
        sb.append(getLanguageClassNameWithoutPkg(data));
        sb.append(" extends ");
        sb.append(addToImports("org.netbeans.spi.lexer.AbstractLanguage"));
        sb.append(" {\n\n");
    }

    protected void appendClassStart(StringBuffer sb, LanguageData data) {

        // Append static get() method
        String langClassNameWithoutPkg = getLanguageClassNameWithoutPkg(data);
        LexerGenUtilities.appendSpaces(sb, 4);
        sb.append("/** Lazily initialized singleton instance of this language. */\n");
        LexerGenUtilities.appendSpaces(sb, 4);
        sb.append("private static ").append(langClassNameWithoutPkg);
        sb.append(" INSTANCE;\n\n");
        LexerGenUtilities.appendSpaces(sb, 4);
        sb.append("/** @return singleton instance of this language. */\n");
        LexerGenUtilities.appendSpaces(sb, 4);
        sb.append("public static synchronized ").append(langClassNameWithoutPkg);
        sb.append(" get() {\n");
        LexerGenUtilities.appendSpaces(sb, 8);
        sb.append("if (INSTANCE == null)\n");
        LexerGenUtilities.appendSpaces(sb, 12);
        sb.append("INSTANCE = new ").append(langClassNameWithoutPkg);
        sb.append("();\n\n");
        LexerGenUtilities.appendSpaces(sb, 8);
        sb.append("return INSTANCE;\n");
        LexerGenUtilities.appendSpaces(sb, 4);
        sb.append("}\n\n");
    }
    
    protected void appendClassConstructor(StringBuffer sb, LanguageData data) {
        LexerGenUtilities.appendSpaces(sb, 4);
        sb.append(getLanguageClassNameWithoutPkg(data));
        sb.append("() {\n");
        LexerGenUtilities.appendSpaces(sb, 4);
        sb.append("}\n\n");
    }

    protected void appendClassEnd(StringBuffer sb, LanguageData data) {
    }
    
    protected void appendLexerMethodBody(StringBuffer sb, LanguageData data) {
        String lexerClassName = data.getLexerClassName();
        addToImports(lexerClassName);
        LexerGenUtilities.appendSpaces(sb, 8);
        sb.append("return new ");
        sb.append(addToImports(lexerClassName));
        sb.append("();\n");
    }
    
    /**
     * Add the class to imports section. This method can be used
     * anytime even during the class source generation
     * because the imports are added to the generated source
     * at the end of the whole process.
     * @param className full name of the class including package name.
     * @return the short name of the class name (without package).
     */
    protected final String addToImports(String className) {
        String[] pkgCls = (String[])imports.get(className);
        if (pkgCls == null) {
            pkgCls = LexerGenUtilities.splitClassName(className);
            imports.put(className, pkgCls);
        }
        return pkgCls[1];
    }

    private void appendImports(StringBuffer sb, LanguageData data) {
        String langClassPkgName = getLanguageClassPkgName(data);
        Set iSet = imports.keySet();
        String[] importClasses = new String[iSet.size()];
        iSet.toArray(importClasses);
        Arrays.sort(importClasses);
        for (int i = 0; i < importClasses.length; i++) {
            String className = importClasses[i];
            String[] pkgCls = (String[])imports.get(className);
            if (!"".equals(pkgCls[0]) && !"java.lang".equals(pkgCls[0])
                && !pkgCls[0].equals(langClassPkgName) // differs from package stmt
            ) {
                sb.append("import ").append(className).append(";\n");
            }
        }
    }
    
}

