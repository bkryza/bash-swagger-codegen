package org.onedata.swagger.codegen;

import io.swagger.codegen.*;
import io.swagger.models.properties.*;
import io.swagger.models.parameters.*;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Swagger;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.io.File;

public class BashClientCodegen extends DefaultCodegen implements CodegenConfig {

  protected String apiVersion = "1.0.0";

  protected boolean processMarkdown = false;

  public static final String CURL_OPTIONS = "curlOptions";
  public static final String PROCESS_MARKDOWN = "processMarkdown";

  /**
   * Configures the type of generator.
   * 
   * @return  the CodegenType for this generator
   * @see     io.swagger.codegen.CodegenType
   */
  public CodegenType getTag() {
    return CodegenType.CLIENT;
  }

  /**
   * Configures a friendly name for the generator.  This will be used by 
   * the generator to select the library with the -l flag.
   * 
   * @return the friendly name for the generator
   */
  public String getName() {
    return "bash";
  }

  /**
   * Returns human-friendly help for the generator.  Provide the consumer with 
   * help tips, parameters here
   * 
   * @return A string value for the help message
   */
  public String getHelp() {
    return "Generates a Bash client script based on cURL.";
  }

  public BashClientCodegen() {
    super();

    // set the output folder here
    outputFolder = "generated-code/bash";

    /**
     * Models.  You can write model files using the modelTemplateFiles map.
     * if you want to create one template for file, you can do so here.
     * for multiple files for model, just put another entry in the 
     * `modelTemplateFiles` with a different extension
     */
    modelTemplateFiles.clear();


    /**
     * Api classes.  You can write classes for each Api file with the 
     * apiTemplateFiles map. As with models, add multiple entries with 
     * different extensions for multiple files per class.
     */
    apiTemplateFiles.clear();


    /**
     * Template Location. This is the location which templates will be read 
     * from. The generator will use the resource stream to attempt to read the 
     * templates.
     */
    templateDir = "bash";


    /**
     * Allow the user to force the script to always include certain cURL
     * comamnds
     */
    cliOptions.add(CliOption.newString(CURL_OPTIONS, "Default cURL options"));
    cliOptions.add(CliOption.newBoolean(PROCESS_MARKDOWN, 
                      "Convert all Markdown Markup into terminal formatting"));


    /**
     * Bash reserved words.
     */
    reservedWords = new HashSet<String> (
      Arrays.asList(
        "case",
        "do",
        "done",
        "elif",
        "else",
        "esac",
        "fi",
        "for",
        "function",
        "if",
        "in",
        "select",
        "then",
        "time",
        "until",
        "while")
    );

    typeMapping.clear();
    typeMapping.put("array", "array");
    typeMapping.put("map", "map");
    typeMapping.put("List", "array");
    typeMapping.put("boolean", "boolean");
    typeMapping.put("string", "string");
    typeMapping.put("int", "integer");
    typeMapping.put("float", "float");
    typeMapping.put("number", "integer");
    typeMapping.put("DateTime", "string");
    typeMapping.put("long", "integer");
    typeMapping.put("short", "integer");
    typeMapping.put("char", "string");
    typeMapping.put("double", "float");
    typeMapping.put("object", "map");
    typeMapping.put("integer", "integer");
    typeMapping.put("ByteArray", "string");
    typeMapping.put("binary", "binary");

    /**
     * Additional Properties.  These values can be passed to the templates and
     * are available in models, apis, and supporting files
     */
    additionalProperties.put("apiVersion", apiVersion);


    /**
     * Supporting Files.  You can write single files for the generator with the
     * entire object tree available.  If the input file has a suffix of 
     * `.mustache it will be processed by the template engine.  Otherwise, 
     * it will be copied
     */
    supportingFiles.add(new SupportingFile(
      "client.mustache",   // the input template or file
      "",                  // the destination folder, relative `outputFolder`
      "client.sh")         // the output file
    );

    /**
     * Language Specific Primitives.  These types will not trigger imports by
     * the client generator
     */
    languageSpecificPrimitives = new HashSet<String>(
      //Arrays.asList()
    );
  }


  @Override
  public void processOpts() {
      super.processOpts();
      String curlopts = "";

      if (additionalProperties.containsKey(CURL_OPTIONS)) {
          curlopts = additionalProperties.get(CURL_OPTIONS).toString();
          additionalProperties.put("curl-codegen-options", curlopts);
      }

      if (additionalProperties.containsKey(PROCESS_MARKDOWN)) {
        this.processMarkdown = true;
      }
      
  }

  /**
   * Escapes a reserved word as defined in the `reservedWords` array. Handle 
   * escaping those terms here. This logic is only called if a variable 
   * matches the reseved words.
   * 
   * @return the escaped term
   */
  @Override
  public String escapeReservedWord(String name) {
    return "_" + name;  // add an underscore to the name
  }

  /**
   * Location to write model files.  You can use the modelPackage() as defined 
   * when the class is instantiated.
   */
  public String modelFileFolder() {
    return outputFolder; 
  }

  /**
   * Location to write api files.  You can use the apiPackage() as defined when 
   * the class is instantiated.
   */
  @Override
  public String apiFileFolder() {
    return outputFolder;
  }


  /**
   * Optional - type declaration. This is a String which is used by the 
   * templates to instantiate your types. There is typically special handling 
   * for different property types
   *
   * @return a string value used as the `dataType` field for model templates, 
   *         `returnType` for api templates
   */
  @Override
  public String getTypeDeclaration(Property p) {
    if(p instanceof ArrayProperty) {
      ArrayProperty ap = (ArrayProperty) p;
      Property inner = ap.getItems();
      return getSwaggerType(p) + "[" + getTypeDeclaration(inner) + "]";
    }
    else if (p instanceof MapProperty) {
      MapProperty mp = (MapProperty) p;
      Property inner = mp.getAdditionalProperties();
      return getSwaggerType(p) + "[String, " + getTypeDeclaration(inner) + "]";
    }
    return super.getTypeDeclaration(p);
  }

  /**
   * Optional - swagger type conversion. This is used to map swagger types in 
   * a `Property` into either language specific types via `typeMapping` or into 
   * complex models if there is not a mapping.
   *
   * @return a string value of the type or complex model for this property
   * @see io.swagger.models.properties.Property
   */
  @Override
  public String getSwaggerType(Property p) {
    String swaggerType = super.getSwaggerType(p);
    String type = null;
    if(typeMapping.containsKey(swaggerType)) {
      type = typeMapping.get(swaggerType);
      if(languageSpecificPrimitives.contains(type))
        return toModelName(type);
    }
    else
      type = swaggerType;
    return toModelName(type);
  }


  /**
   * Convert Swagger Parameter object to Codegen Parameter object
   *
   * @param param Swagger parameter object
   * @param imports set of imports for library/package/module
   * @return Codegen Parameter object
   */
  @Override
  public CodegenParameter fromParameter(Parameter param, Set<String> imports) {

    CodegenParameter p = super.fromParameter(param, imports);

    if(param instanceof BodyParameter) {

      Model model = ((BodyParameter)param).getSchema();

    }


    return p;

  }

  /**
   * Override with any special text escaping logic
   */ 
  @SuppressWarnings("static-method")
  public String escapeText(String input) {
      if (input == null) {
          return input;
      }

      /**
       * replace \ with \
       *
       * replace " with \"
       * outter unescape to retain the original multi-byte characters
       */
      String result = escapeUnsafeCharacters(
        StringEscapeUtils.unescapeJava(
          StringEscapeUtils.escapeJava(input).replace("\\/", "/"))
                     .replace("\\", "\\\\")
                     .replace("\"", "\\\""));

      if(this.processMarkdown) {

        /**
         * Convert markdown strong **Bold text**  and __Bold text__
         * to bash bold control sequences (tput bold)
         */
        result = result.replaceAll("(?m)(^|\\s)\\*{2}([\\w\\d ]+)\\*{2}($|\\s)", 
                                   "\\$\\(tput bold\\) $2 \\$\\(tput sgr0\\)");
        result = result.replaceAll("(?m)(^|\\s)_{2}([\\w\\d ]+)_{2}($|\\s)", 
                                   "\\$\\(tput bold\\) $2 \\$\\(tput sgr0\\)");
        
        /**
         * Convert markdown *Italics text* and _Italics text_ to bash dim 
         * control sequences (tput dim)
         */
        result = result.replaceAll("(?m)(^|\\s)\\*{1}([\\w\\d ]+)\\*{1}($|\\s)", 
                                   "\\$\\(tput dim\\) $2 \\$\\(tput sgr0\\)");
        result = result.replaceAll("(?m)(^|\\s)_{1}([\\w\\d ]+)_{1}($|\\s)", 
                                   "\\$\\(tput dim\\) $2 \\$\\(tput sgr0\\)");


        /**
         * Convert all markdown section 1 level headers with bold
         */ 
        result.replaceAll("(?m)^\\#\\s+(.+)$",
                          "\\$\\(tput bold\\)$1\\$\\(tput sgr0\\)");

        /**
         * Convert all markdown section 2 level headers with bold
         */ 
        result.replaceAll("(?m)^\\#\\#\\s+(.+)$",
                          "\\$\\(tput bold\\)$1\\$\\(tput sgr0\\)");

        /**
         * Convert all markdown section 3 level headers with bold
         */ 
        result.replaceAll("(?m)^\\#\\#\\#\\s+(.+)$",
                          "\\$\\(tput bold\\)$1\\$\\(tput sgr0\\)");
      }

      return result;
  }

  @Override
  public String escapeQuotationMark(String input) {
      return input;
  }
  
  /**
   * Override with any special text escaping logic to handle unsafe
   * characters so as to avoid code injection.
   * 
   * @param input String to be cleaned up
   * @return string with unsafe characters removed or escaped
   */
  public String escapeUnsafeCharacters(String input) {

    /**
     * Replace backticks to normal single quotes.
     */
    String result = input.replaceAll("`", "'");

    return result;
  
  }


  @Override
  public CodegenOperation fromOperation(String path, String httpMethod, 
                                        Operation operation, 
                                        Map<String, Model> definitions, 
                                        Swagger swagger) {

      CodegenOperation op = super.fromOperation(path, httpMethod, operation, 
                                                definitions, swagger);

      for (CodegenParameter p : op.bodyParams) {
        if(p.dataType != null && definitions.get(p.dataType) != null) {
          p.vendorExtensions.put(
            "x-codegen-body-example", 
              definitions.get(p.dataType).getExample()
            ); 
        }
      }

      return op;

  }

  /**
   * Preprocess original properties from the Swagger definition where necessary.
   * 
   * @param swagger [description]
   */
  @Override
  public void preprocessSwagger(Swagger swagger) {
      super.preprocessSwagger(swagger);
      if ("/".equals(swagger.getBasePath())) {
          swagger.setBasePath("");
      }
  }

}