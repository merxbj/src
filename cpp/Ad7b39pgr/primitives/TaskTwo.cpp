#include "TaskTwo.h"


TaskTwo::TaskTwo(void)
{
}


TaskTwo::~TaskTwo(void)
{
}

void TaskTwo::Initialize()
{
    CheckOpenGl();

    GLuint vertexBuffer = InitializeVertexBufferHouse();
    GLuint vertexColorsBuffer = InitializeVertexBufferHouseColors();

    m_uiVertexArrayHandle = MakeSingleVertexArray();
    AssignBufferToAttribute(m_uiVertexArrayHandle, m_iPositionLocation, 2, GL_ARRAY_BUFFER, vertexBuffer, sizeof(GLfloat) * 2, 0);
    AssignBufferToAttribute(m_uiVertexArrayHandle, m_iColorLocation, 4, GL_ARRAY_BUFFER, vertexColorsBuffer, sizeof(GLfloat) * 4, 0);

    CheckOpenGl();
}

/**
    VBO's with vertices and colors
*/
GLuint TaskTwo::InitializeVertexBufferHouse()
{
    // buffer with vertices
    static const GLfloat vertices[] =  {
        -0.6f,  0.6f, // base
        -0.6f, -0.6f,
         0.6f,  0.6f,
         0.6f, -0.6f,

        -0.6f,  0.6f, // roof
         0.6f,  0.6f,
         0.0f,  0.9f,

         0.35f,  0.725f, // chimney
         0.25f,  0.775f, 
         0.35f,  1.0f,
         0.25f,  1.0f,
    };

    return MakeBuffer(GL_ARRAY_BUFFER, vertices, sizeof(vertices));
};

/**
    VBO's with vertices and colors
*/
GLuint TaskTwo::InitializeVertexBufferHouseColors()
{   
    // buffer with vertices
    static const GLfloat colors[] = {
        1.0f, 1.0f, 0.0f, 1.0f, // base
        1.0f, 1.0f, 0.0f, 1.0f,
        1.0f, 1.0f, 0.0f, 1.0f,
        1.0f, 1.0f, 0.0f, 1.0f,

        1.0f, 0.0f, 0.0f, 1.0f, // roof
        1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f, 1.0f,

        0.0f, 0.0f, 1.0f, 1.0f, // chimney
        0.0f, 0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f,
    };

    return MakeBuffer(GL_ARRAY_BUFFER, colors, sizeof(colors));
};

void TaskTwo::DrawTask()
{
    SetRotationMatrix();
    glBindVertexArray(m_uiVertexArrayHandle);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4); // base
    glDrawArrays(GL_TRIANGLES, 4, 3); // roof
    glDrawArrays(GL_TRIANGLE_STRIP, 7, 4); // chimney
    glBindVertexArray(0);
}

const char* TaskTwo::ToString()
{
    return "Task Two";
}
