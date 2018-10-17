package koji.swagger;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AnnotateControllerAction extends AnAction {
    private Project currentProject;
    private Document currentDocument;
    private PsiFile currentPSI;
    private PsiElementFactory currentFactory;


    @Override
    public void actionPerformed(AnActionEvent event) {
        currentProject = event.getRequiredData(CommonDataKeys.PROJECT);
            Editor editor = event.getData(PlatformDataKeys.EDITOR);
            if (editor == null)
                return;



        currentDocument = editor.getDocument();
        currentPSI = PsiDocumentManager.getInstance(currentProject).getPsiFile(currentDocument);
        currentFactory = JavaPsiFacade.getInstance(currentProject).getElementFactory();

        if (currentPSI instanceof PsiJavaFile)
            process(((PsiJavaFile) currentPSI).getClasses()[0]);



    }


    private void process(PsiClass clazz) {
        Runnable runnable = new Runnable() {
            public void run() {
                for (PsiMethod method : clazz.getMethods()) {
                    try {

                        //TODO:ci sara qualche helper che lo fa sicuro...
                        PsiAnnotation oldAnnotation = method.getAnnotation("ApiResponses");
                        if (oldAnnotation != null)
                            oldAnnotation.delete();

                        PsiReturnStatement[] returns = PsiUtil.findReturnStatements(method);


                        if (returns != null && returns.length > 0) {
                            List<String> r = new ArrayList<>();
                            for (PsiReturnStatement ret : returns) {
                                PsiNewExpression[] ne = PsiTreeUtil.getChildrenOfType(ret, PsiNewExpression.class);
                                if (ne != null && ne.length == 1) {
                                    PsiNewExpression n = ne[0];
                                    if ("ResponseEntity".equals(n.getClassReference().getQualifiedName())) {
                                        if (n.getArgumentList() != null) {
                                            PsiExpression[] exps = n.getArgumentList().getExpressions();
                                            PsiExpression ret_value = exps[0];
                                            PsiExpression ret_status = exps[1];
                                            String ret_class;

                                            String an_status, an_message;
                                            switch (ret_status.getText()) {
                                                case "HttpStatus.OK":
                                                    an_status = "HttpServletResponse.SC_OK";
                                                    an_message = "OK";
                                                    ret_class = extractType(method.getReturnTypeElement());
                                                    r.add(String.format("@ApiResponse(code = %s, message = \"%s\", response = %s)", an_status, an_message, ret_class + ".class"));
                                                    break;
                                                case "HttpStatus.INTERNAL_SERVER_ERROR":
                                                    an_status = "HttpServletResponse.SC_INTERNAL_SERVER_ERROR";
                                                    an_message = "Exception Message";
                                                    r.add(String.format("@ApiResponse(code = %s, message = \"%s\")", an_status, an_message));
                                                    break;
                                                case "HttpStatus.BAD_REQUEST":
                                                    an_status = "HttpServletResponse.SC_BAD_REQUEST";
                                                    an_message = "Exception Message";
                                                    r.add(String.format("@ApiResponse(code = %s, message = \"%s\")", an_status, an_message));
                                                    break;
                                                default:
                                                    an_status = "";
                                                    an_message = "";
                                            }
                                        }

                                    }


                                }


                                //    r.add(String.format("@ApiResponse(code = %s, message = \"%s\", response = %s)", "HttpServletResponse.SC_BAD_REQUEST", "ok", "ContractList.class"));
                            }
                            PsiAnnotation responses = currentFactory.createAnnotationFromText(String.format("@ApiResponses(value={%s})", StringUtils.join(r, ",")), null);
                            clazz.addBefore(responses, method);
                        }


                    } catch (Exception ex) {
                        ex.printStackTrace();

                    }

                }
            }
        };

        WriteCommandAction.runWriteCommandAction(currentProject, runnable);


    }


    private String extractType(PsiTypeElement retType) {
        PsiJavaCodeReferenceElement ref = retType.getInnermostComponentReferenceElement();

        if (ref != null && ref.getTypeParameters() != null && ref.getTypeParameters().length > 0)
            return ref.getTypeParameters()[0].getCanonicalText();
        else
            return retType.getText();


    }
}