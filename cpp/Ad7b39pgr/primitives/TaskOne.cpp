#include "TaskOne.h"

TaskOne::TaskOne(void)
{
    m_strVertexShader = 
	    "#version 130\n"
	    "uniform mat4 spinMatrix;\n"
	    "in vec4 position;\n"
	    "void main()\n"
	    "{\n"
	    "	gl_Position = spinMatrix * position;\n"
	    "}\n";

    m_strFragmentShader =
	    "#version 130\n"
	    "out vec4 outputColor;\n"
	    "void main()\n"
	    "{\n"
	    "	outputColor = vec4(1.0f, 1.0f, 1.0f, 1.0f);\n"
	    "}\n";
}

TaskOne::~TaskOne(void)
{

}

void TaskOne::Initialize()
{
    CheckOpenGl();
    m_uiVertexArrayHandle = MakeSingleVertexArray();
    GLuint vertexBuffer = InitializeVertexBufferHouse();
    AssignBufferToAttribute(m_uiVertexArrayHandle, m_iPositionLocation, 2 /* 2D */, GL_ARRAY_BUFFER, vertexBuffer, sizeof(GLfloat) * 2, 0);
    CheckOpenGl();
}

void TaskOne::ConfigureShaders(ShaderVectorPtr shaders)
{
    if (shaders != NULL)
    {
        shaders->push_back(pgr::createShader(GL_VERTEX_SHADER, m_strVertexShader));
        shaders->push_back(pgr::createShader(GL_FRAGMENT_SHADER, m_strFragmentShader));
    }
}

void TaskOne::DrawTask()
{
    SetRotationMatrix();
    glBindVertexArray(m_uiVertexArrayHandle);
    glDrawArrays(GL_LINE_LOOP, 0, 7); /* TODO: Replace 7 with variable */
    glBindVertexArray(0);
}

/**
    VBO's with vertices and colors
*/
GLuint TaskOne::InitializeVertexBufferHouse()
{   
    // buffer with vertices
    static const GLfloat vertices[] =  {
        -0.6f,  0.6f,
        -0.6f, -0.6f,
         0.6f, -0.6f,
         0.6f,  0.6f,
        -0.6f,  0.6f,
         0.0f,  0.9f,
         0.6f,  0.6f
    };

    return MakeBuffer(GL_ARRAY_BUFFER, vertices, sizeof(vertices));
};

const char* TaskOne::ToString()
{
    return "Task One";
}