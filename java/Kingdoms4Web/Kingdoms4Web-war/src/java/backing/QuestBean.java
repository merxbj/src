/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backing;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import model.entity.Quest;
import model.session.QuestFacade;

/**
 *
 * @author eTeR
 */
@Named
@SessionScoped
public class QuestBean implements Serializable {
    
    @EJB
    protected QuestFacade questFacade;
    
    /** Creates a new instance of QuestBean */
    public QuestBean() {
       
    }
    
    @PostConstruct
    protected void init() {
    }

    public void remove(Quest entity) {
        questFacade.remove(entity);
    }

    public List<Quest> findRange(int[] range) {
        return questFacade.findRange(range);
    }

    public List<Quest> findAll() {
        return questFacade.findAll();
    }

    public Quest find(Object id) {
        return questFacade.find(id);
    }

    public void edit(Quest entity) {
        questFacade.edit(entity);
    }

    public void create(Quest entity) {
        questFacade.create(entity);
    }

    public int count() {
        return questFacade.count();
    }

}
