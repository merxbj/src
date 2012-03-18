#include "TaskBase.h"

TaskBase::TaskBase(void)
{
    this->m_fSpinAngle = 0.0f;
    this->m_uiVertexArrayHandle = 0;
}

TaskBase::~TaskBase(void)
{
}

// -------------------------------------------------------------------
// Rotation around Y axis

void TaskBase::SetRotationMatrix()
{
    pgr::Matrix4f matrix = pgr::Matrix4f::FromScale(pgr::Vec3f(0.5f, 0.5f, 0.5f));
    matrix.rotate(m_fSpinAngle, pgr::Vec3f(0, 1, 0));

    // Setting the matrix to the vertex shader
    glUniformMatrix4fv(m_iSpinMatrixLocation, 1, GL_FALSE, matrix);
    return;
}

/**
    Makes a named GPU buffer, sends the data to this buffer and
    returns the name of this buffer to be accessed later on.
*/
GLuint TaskBase::MakeBuffer(GLenum target, const void* bufferData, GLsizei bufferSize)
{
    GLuint buffer;
    glGenBuffers(1, &buffer);
    glBindBuffer(target, buffer);
    glBufferData(target, bufferSize, bufferData, GL_STATIC_DRAW);
    glBindBuffer(target, 0);
    return buffer;
}

GLuint TaskBase::MakeSingleVertexArray()
{
    GLuint array;
    glGenVertexArrays(1, &array);
    return array;
}

void TaskBase::AssignBufferToAttribute(GLuint vao, GLuint location, GLint size, GLenum target, GLuint vertexBuffer, GLsizei stride, GLuint offset)
{
    glBindVertexArray(vao);
    glBindBuffer(target, vertexBuffer);
    glEnableVertexAttribArray(location);
    glVertexAttribPointer(location, size, GL_FLOAT, GL_FALSE, stride, (void*) offset);
    glBindBuffer(target, 0);
    glBindVertexArray(0);
}

void TaskBase::InitializeProgram()
{
    CheckOpenGl();

    ShaderVector shaderList;
    ConfigureShaders(&shaderList);

    CheckOpenGl();

    // Create the program with shaders
    m_uiProgramHandle = pgr::createProgram(shaderList);

    CheckOpenGl();

    // get attributes and uniform locations
    m_iPositionLocation = glGetAttribLocation(m_uiProgramHandle, "position");
    m_iSpinMatrixLocation = glGetUniformLocation(m_uiProgramHandle, "spinMatrix");

    CheckOpenGl();
}

void TaskBase::Draw()
{
    CheckOpenGl();

    glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    glUseProgram(m_uiProgramHandle);
    DrawTask();
    glUseProgram(0);

    CheckOpenGl();
}

void TaskBase::Update()
{
    m_fSpinAngle += SMALL_ANGLE;
}

void TaskBase::CheckOpenGl()
{
    GLenum error; 
    if ((error = glGetError()) != GL_NO_ERROR) 
    { 
        throw OpenGlException(error); 
    }
}