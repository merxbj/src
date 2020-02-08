// A7B39PGR. 2012, Petr Felkel 
//----------------------------------------------------------------------------------------
/**
 * \file       primitives.cpp
 * \author     Petr Felkel 
 * \date       2010/02/27
 * \brief      Application sketeton that will be used for solution of tasks concerning geometric primitives.
 *
*/
//----------------------------------------------------------------------------------------

// this is the example solution

#include <iostream>
#include <GL/glew.h>
#include <vector>
#include "TaskOne.h"
#include "TaskTwo.h"
#include "TaskThree.h"
#include "TaskFour.h"
#include "BonusTask.h"

#include <GL/freeglut.h>    // glut + new freeglut functions - like OpenGL Version and profile

/// Main window label.
#define TITLE  "Seminar 3 - Graphical Primitives"

/// OpenGL version
const int MAJOR_VERSION = 3;
const int MINOR_VERSION = 3;
const int PROFILE = GLUT_CORE_PROFILE;  // applicable from OpenGL 3.2

/// Main window initial width in pixels.
const int WIDTH = 500;

/// Main window initial width in pixels.
const int HEIGHT = 500;

///animation time step for glutTimer
const int TIMER_STEP = 20;   // next event in [ms]

/// Indicates whether house rotation is enabled or disabled. 
bool spinFlag = true;

/// Indicates if the back face culling is enabled.
bool cullFlag = false;

/// Indicates, if the GL_LINE FILL mode is used
bool lineModeFlag = false;

// Task book keeping - TODO: Move to some class
typedef std::vector<ITask*> TaskVector;
TaskVector tasks;
int currentTaskNumber = 0;


//Called to update the display.
//You should call glutSwapBuffers after all of your rendering to display what you rendered.
//If you need continuous updates of the screen, call glutPostRedisplay() at the end of the function.
void display()
{
    if (cullFlag)
    {
        glEnable(GL_CULL_FACE);
    }
    else
    {
        glDisable(GL_CULL_FACE);
    }

    if (lineModeFlag)
    {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }
    else
    {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    ITask* currentTask = tasks[currentTaskNumber];
    if (currentTask != NULL)
    {
        currentTask->Draw();
    }

    // bind 1-3
    glutSwapBuffers();
}

//Called whenever the window is resized. The new window size is given, in pixels.
//This is an opportunity to call glViewport or glScissor to keep up with the change in size.
void reshape(int w, int h)
{
    glViewport(0, 0, (GLsizei) w, (GLsizei) h);
}

void FuncTimerCallback(int value)
{
    if (spinFlag)
    {
        glutTimerFunc(TIMER_STEP, FuncTimerCallback, 0);  //333 (1%) -> 3 -> (14%)

        ITask* currentTask = tasks[currentTaskNumber];
        if (currentTask != NULL)
        {
            currentTask->Update();
        }
    }
    glutPostRedisplay();
}

//Called whenever a key on the keyboard was pressed.
//The key is given by the ''key'' parameter, which is in ASCII.
//It's often a good idea to have the escape key (ASCII value 27) call glutLeaveMainLoop() to
//exit the program.
void myKeyboard(unsigned char key, int x, int y)
{
    switch (key)
    {
        case 27:
            exit(0);
            break;
        case ' ':
            currentTaskNumber = (currentTaskNumber + 1) % tasks.size();
            ITask* currentTask = tasks[currentTaskNumber];
            if (currentTask != NULL)
            {
                currentTask->InitializeProgram();
                printf("Current Task = %s\n", currentTask->ToString());
            }
            break;
    }
    glutPostRedisplay();  // Nutno, kdybychom nevolali prekresleni casovacem ci Idle
}

//Called after the window and OpenGL are initialized. Called exactly once, before the main loop.
void init()
{
    tasks.push_back(new TaskOne());
    tasks.push_back(new TaskTwo());
    tasks.push_back(new TaskThree());
    tasks.push_back(new TaskFour());
    tasks.push_back(new BonusTask());

    // initialize all our new tasks
    for (TaskVector::iterator it = tasks.begin(); it != tasks.end(); it++)
    {
        ITask* task = (*it);
        task->InitializeProgram();
        task->Initialize();
    }

    currentTaskNumber = 0;
    ITask* currentTask = tasks[currentTaskNumber];
    if (currentTask != NULL)
    {
        currentTask->InitializeProgram();
    }

    glCullFace(GL_BACK);  // only back faces will be culled, if enabled
    glClearColor(0.3f, 0.3f, 0.3f, 1.0f); // color for cleaning the screen
}

void uninit()
{
    for (TaskVector::iterator it = tasks.begin(); it != tasks.end(); it++)
    {
        delete *it;
    }
    tasks.clear();
}

void myMenu(int item)
{
    switch (item) {
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
        {
            currentTaskNumber = item - 1;
            ITask* currentTask = tasks[currentTaskNumber];
            if (currentTask != NULL)
            {
                currentTask->InitializeProgram();
                printf("Current Task = %s\n", currentTask->ToString());
            }
        }
        break;
    case 10: 
        spinFlag = !spinFlag;
        if (spinFlag)
        {
            glutTimerFunc(TIMER_STEP, FuncTimerCallback, 0);  
        }
        break;
    case 11:
        cullFlag = !cullFlag;  
        glutPostRedisplay();
        break;
    case 12:
        lineModeFlag = !lineModeFlag;  
        glutPostRedisplay();
        break;
    case 99:
        exit(0);
        break;    // never reached
    }

    glutPostRedisplay();  // Nutno, kdybychom nevolali prekresleni casovacem ci Idle
}

void createMenu(void)
{
    int submenuID = glutCreateMenu( myMenu );
    glutAddMenuEntry("Task 1", 1);
    glutAddMenuEntry("Task 2", 2);
    glutAddMenuEntry("Task 3", 3);
    glutAddMenuEntry("Task 4", 4);
    glutAddMenuEntry("Bonus", 5);

    /* Create main menu. */
    glutCreateMenu(myMenu); 
    glutAddSubMenu("Task Selection", submenuID );
    glutAddMenuEntry("Rotation on/off", 10);
    glutAddMenuEntry("BackFace Culling on/off", 11);
    glutAddMenuEntry("Polygon Fill mode line/fill", 12 );
    glutAddMenuEntry("Quit", 99);

    /* Menu will be invoked by the right mouse button */
    glutAttachMenu(GLUT_RIGHT_BUTTON);  

}

void myMouse(int button, int state, int x, int y )
{
    if ((button == GLUT_LEFT_BUTTON) && (state == GLUT_DOWN))
    {
        spinFlag = !spinFlag;
        if (spinFlag)
        {
            glutTimerFunc(TIMER_STEP, FuncTimerCallback, 0);  
        }
        glutPostRedisplay();  // Nutno, kdybychom nevolali prekresleni casovacem ci Idle
    }
}


/*
 * Entry point
 */
int main(int argc, char** argv)
{
    try
    {
        /* Initialize the GLUT library. */
        glutInit(&argc, argv);

        glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGBA | GLUT_DEPTH);
        glutInitWindowPosition(5, 5);
        glutInitWindowSize(WIDTH, HEIGHT);

        /* Create main window and set callbacks. */
        glutCreateWindow(TITLE);
        glutDisplayFunc(display);
        glutReshapeFunc(reshape);
        glutKeyboardFunc(myKeyboard);
        glutMouseFunc(myMouse);  // uloha 6
        if (spinFlag)
        {
            glutTimerFunc(33, FuncTimerCallback, 0);
        }

        createMenu();

        // load the pointers to OpenGL functions (only needed in MS Windows)
        glewInit();
        if (!GLEW_VERSION_3_0) {
            fprintf(stderr, "OpenGL 3.0 or higher not available\n");
            return 1;
        }

        // Init context and profile 
        glutInitContextVersion(MAJOR_VERSION, MINOR_VERSION);
        if ((MAJOR_VERSION >= 3) && (MINOR_VERSION >= 2)) 
          glutInitContextProfile(PROFILE);   // core or compatibiliy, as defined in OpenGL 3.2

        init();

        glutMainLoop();
    }
    catch (const OpenGlException& ex)
    {
        std::cerr << "OpenGL reported an error: " << ex.GetGlError() << std::endl;
    }

    uninit();

    system("PAUSE");
    return 0;
}
