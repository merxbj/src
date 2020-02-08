#include "AdvancedTask.h"


AdvancedTask::AdvancedTask(void)
{
    m_strVertexShader = 
	    "#version 130\n"
	    "uniform mat4 spinMatrix;\n"
	    "in vec4 position;\n"
	    "in vec4 color;\n"
	    "smooth out vec4 theColor;\n"	
	    "void main()\n"
	    "{\n"
	    "	gl_Position = spinMatrix * position;\n"
	    "	theColor = color;\n"
	    "}\n";

    m_strFragmentShader =
        "#version 130\n"
	    "smooth in vec4 theColor;\n"
	    "out vec4 outputColor;\n"
	    "void main()\n"
	    "{\n"
	    "	outputColor = theColor;\n"
	    "}\n";
}

void AdvancedTask::InitializeProgram()
{
    CheckOpenGl();

    TaskBase::InitializeProgram();
    m_iColorLocation = glGetAttribLocation(m_uiProgramHandle, "color");

    CheckOpenGl();
}

void AdvancedTask::ConfigureShaders(ShaderVectorPtr shaders)
{
    if (shaders != NULL)
    {
        shaders->push_back(pgr::createShader(GL_VERTEX_SHADER, m_strVertexShader));
        shaders->push_back(pgr::createShader(GL_FRAGMENT_SHADER, m_strFragmentShader));
    }
}
