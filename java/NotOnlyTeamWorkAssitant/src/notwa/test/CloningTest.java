package notwa.test;

import notwa.wom.*;

public class CloningTest {

	public CloningTest() {
		fillWithSomeData();
	}

	private void fillWithSomeData() {
		User user = new User(1);
		user.setLoginName("mrneo");
		
		Project project = new Project(1);
		project.setProjectName("notwa");
		
		Note note = new Note(1);
		note.setAuthor(user);
		note.setNoteText("kurva jedna zasrana");
		
		NoteCollection noteCollection = new NoteCollection();
		noteCollection.add(note);
		
		
		WorkItem wi = new WorkItem(1);
		wi.setSubject("class CloningTest");
		wi.setAssignedUser(user);
		wi.setDescription("create new class CloningTest for testing this code...");
		wi.setProject(project);
		wi.setNoteCollection(noteCollection);
		
		WorkItemCollection wic = new WorkItemCollection();
		wic.add(wi);
	}
}
