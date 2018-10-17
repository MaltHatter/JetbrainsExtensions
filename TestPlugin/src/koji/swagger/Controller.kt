package koji.swagger

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.util.PsiUtil
import javax.swing.Icon


class Controller : AnAction {
    constructor(text: String?, desc: String?, icon: Icon?) : super(text, desc, icon)
    constructor(text: String?) : super(text)
    constructor(icon: Icon?) : super(icon)

    fun getCurrentProject(event: AnActionEvent?): Project? = event?.getRequiredData(CommonDataKeys.PROJECT)
    fun getCurrentDocument(event: AnActionEvent?): Document? = event?.getData(PlatformDataKeys.EDITOR)?.document
    fun getPSI(event: AnActionEvent?): PsiFile? = getCurrentDocument(event)?.let { PsiDocumentManager.getInstance(getCurrentProject(event)).getPsiFile(it) }


    override fun actionPerformed(event: AnActionEvent?) {
        val currentProject:Project? = getCurrentProject(event)
        val currentDocument:Document? = getCurrentDocument(event)
        val currentFile: VirtualFile? = currentDocument?.let { FileDocumentManager.getInstance().getFile(it) }

        currentProject.let {p->
            currentFile.let {d->
                PsiManager.getInstance(p).findFile(d)
            }
        }




        // val currentProject: Project = event.getRequiredData(CommonDataKeys.PROJECT)


        val currentPSI = currentDocument?.let { PsiDocumentManager.getInstance(currentProject).getPsiFile(it) }

        if (currentPSI is PsiJavaFile)
            process(currentPSI.classes[0])

        val currentFactory = JavaPsiFacade.getInstance(currentProject)?.elementFactory


    }

    fun process(clazz: PsiClass) {
        run {
            clazz.methods.forEach {
                it.getAnnotation("ApiResponses")?.delete()

                PsiUtil.findReturnStatements(it).forEach { ret ->
                    val ne: PsiNewExpression[] = PsiTreeUtil.getChildrenOfType(ret, PsiNewExpression::class.java)


                }


            }


        }

    }


}