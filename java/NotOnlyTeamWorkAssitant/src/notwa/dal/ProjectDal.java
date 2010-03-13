package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.sql.ParameterCollection;
import notwa.wom.Project;
import notwa.wom.ProjectCollection;

public class ProjectDal extends DataAccessLayer implements Fillable<ProjectCollection>, Getable<Project> {

    public ProjectDal(ConnectionInfo ci) {
        super(ci);
    }

    @Override
    public int Fill(ProjectCollection boc, ParameterCollection pc) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int Fill(ProjectCollection boc) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Project get(ParameterCollection primaryKey) {
        // TODO Auto-generated method stub
        return new Project(1);
    }
}
