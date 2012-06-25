/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package backing;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import model.control.remote.AccountableControl;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author eTeR
 */
@ManagedBean
@RequestScoped
public class UploadBean {

    @ManagedProperty(value="#{securityBean}")
    private SecurityBean security;

    @EJB
    AccountableControl accountables;

    public void setSecurity(SecurityBean security) {
        this.security = security;
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile uploadedFile = event.getFile();
        try {
            accountables.importFromFile(uploadedFile.getContents(), security.getLoggedInUser());
            FacesMessage msg = new FacesMessage("Succesful", uploadedFile.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception ex) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", uploadedFile.getFileName() + " was not uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

}
