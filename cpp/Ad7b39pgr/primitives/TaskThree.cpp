#include "TaskThree.h"

TaskThree::TaskThree(void)
{
}


TaskThree::~TaskThree(void)
{
}

void TaskThree::Initialize()
{
    CheckOpenGl();

    GLuint vertexBuffer = InitializeVertexBufferHouseWithColors();

    m_uiVertexArrayHandle = MakeSingleVertexArray();
    AssignBufferToAttribute(m_uiVertexArrayHandle, m_iPositionLocation, 2, GL_ARRAY_BUFFER, vertexBuffer, sizeof(GLfloat) * 6, 0);
    AssignBufferToAttribute(m_uiVertexArrayHandle, m_iColorLocation, 4, GL_ARRAY_BUFFER, vertexBuffer, sizeof(GLfloat) * 6, 2 * sizeof(GLfloat));

    CheckOpenGl();
}

/**
    VBO's with vertices and colors
*/
GLuint TaskThree::InitializeVertexBufferHouseWithColors()
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
         0.35f,  1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
         0.25f,  1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
    };

    return MakeBuffer(GL_ARRAY_BUFFER, vertices, sizeof(vertices));
};

void TaskThree::DrawTask()
{
    SetRotationMatrix();
    glBindVertexArray(m_uiVertexArrayHandle);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4); // base
    glDrawArrays(GL_TRIANGLES, 4, 3); // roof
    glDrawArrays(GL_TRIANGLE_STRIP, 7, 4); // chimney
    glBindVertexArray(0);
}

const char* TaskThree::ToString()
{
    return "Task Three";
}
