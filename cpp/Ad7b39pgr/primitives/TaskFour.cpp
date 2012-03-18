#include "TaskFour.h"

TaskFour::TaskFour(void)
{
}


TaskFour::~TaskFour(void)
{
}

void TaskFour::Initialize()
{
    CheckOpenGl();

    GLuint vertexBuffer = InitializeVertexBufferHouseWithColors();
    m_uiElementsBuffer = InitializeElementBuffer();

    m_uiVertexArrayHandle = MakeSingleVertexArray();
    AssignBufferToAttribute(m_uiVertexArrayHandle, m_iPositionLocation, 2, GL_ARRAY_BUFFER, vertexBuffer, sizeof(GLfloat) * 6, 0);
    AssignBufferToAttribute(m_uiVertexArrayHandle, m_iColorLocation, 4, GL_ARRAY_BUFFER, vertexBuffer, sizeof(GLfloat) * 6, 2 * sizeof(GLfloat));

    CheckOpenGl();
}

/**
    VBO's with vertices and colors
*/
GLuint TaskFour::InitializeVertexBufferHouseWithColors()
{
    // buffer with vertices
    static const GLfloat vertices[] =  {
        -0.6f,  0.6f, 1.0f, 1.0f, 0.0f, 1.0f,// base
        -0.6f, -0.6f, 1.0f, 1.0f, 0.0f, 1.0f,
         0.6f,  0.6f, 1.0f, 1.0f, 0.0f, 1.0f,
         0.6f, -0.6f, 1.0f, 1.0f, 0.0f, 1.0f,

        -0.6f,  0.6f, 1.0f, 0.0f, 0.0f, 1.0f, // roof
         0.6f,  0.6f, 1.0f, 0.0f, 0.0f, 1.0f,
         0.0f,  0.9f, 1.0f, 0.0f, 0.0f, 1.0f,

         0.35f,  0.725f, 0.0f, 0.0f, 1.0f, 1.0f, // chimney
         0.25f,  0.775f, 0.0f, 0.0f, 1.0f, 1.0f,
         0.35f,  1.000f, 0.0f, 0.0f, 1.0f, 1.0f,
         0.25f,  1.000f, 0.0f, 0.0f, 1.0f, 1.0f,
    };

    return MakeBuffer(GL_ARRAY_BUFFER, vertices, sizeof(vertices));
};

GLuint TaskFour::InitializeElementBuffer()
{
    static const GLushort elements[] =  {
        0,1,2,3,4,5,6,7,8,9,10
    };

    return MakeBuffer(GL_ELEMENT_ARRAY_BUFFER, elements, sizeof(elements));
}

void TaskFour::DrawTask()
{
    SetRotationMatrix();
    glBindVertexArray(m_uiVertexArrayHandle);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_uiElementsBuffer);
    glDrawElements(GL_TRIANGLE_STRIP, 4, GL_UNSIGNED_SHORT, (void*) 0); // base
    glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_SHORT, (void*) (4 * sizeof(GLushort))); // roof
    glDrawElements(GL_TRIANGLE_STRIP, 4, GL_UNSIGNED_SHORT, (void*) (7 * sizeof(GLushort))); // chimney
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glBindVertexArray(0);
}

const char* TaskFour::ToString()
{
    return "Task Four";
}
