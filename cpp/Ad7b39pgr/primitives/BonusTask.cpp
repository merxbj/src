#include "BonusTask.h"

BonusTask::BonusTask(void)
{
}


BonusTask::~BonusTask(void)
{
}

void BonusTask::Initialize()
{
    CheckOpenGl();

    GLuint vertexBuffer = InitializeVertexBufferHouseWithColors();
    m_uiElementsBuffer = InitializeElementBuffer();

    m_uiVertexArrayHandle = MakeSingleVertexArray();
    AssignBufferToAttribute(m_uiVertexArrayHandle, m_iPositionLocation, 3, GL_ARRAY_BUFFER, vertexBuffer, sizeof(GLfloat) * 7, 0);
    AssignBufferToAttribute(m_uiVertexArrayHandle, m_iColorLocation, 4, GL_ARRAY_BUFFER, vertexBuffer, sizeof(GLfloat) * 7, 3 * sizeof(GLfloat));

    CheckOpenGl();
}

/**
    VBO's with vertices and colors
*/
GLuint BonusTask::InitializeVertexBufferHouseWithColors()
{
    // buffer with vertices
    static const GLfloat vertices[] =  {
        -0.6f, -0.6f,  0.6f, 1.0f, 1.0f, 0.0f, 1.0f, // base
         0.6f, -0.6f,  0.6f, 1.0f, 1.0f, 0.0f, 1.0f,
         0.6f, -0.6f, -0.6f, 1.0f, 1.0f, 0.0f, 1.0f,
        -0.6f, -0.6f, -0.6f, 1.0f, 1.0f, 0.0f, 1.0f,
        -0.6f,  0.6f,  0.6f, 1.0f, 1.0f, 0.0f, 1.0f,
         0.6f,  0.6f,  0.6f, 1.0f, 1.0f, 0.0f, 1.0f,
         0.6f,  0.6f, -0.6f, 1.0f, 1.0f, 0.0f, 1.0f,
        -0.6f,  0.6f, -0.6f, 1.0f, 1.0f, 0.0f, 1.0f,
        
        -0.6f,  0.6f,  0.6f, 1.0f, 0.0f, 0.0f, 1.0f, // roof
         0.6f,  0.6f,  0.6f, 1.0f, 0.0f, 0.0f, 1.0f,
         0.6f,  0.6f, -0.6f, 1.0f, 0.0f, 0.0f, 1.0f,
        -0.6f,  0.6f, -0.6f, 1.0f, 0.0f, 0.0f, 1.0f,
         0.0f,  0.9f,  0.6f, 1.0f, 0.0f, 0.0f, 1.0f,
         0.0f,  0.9f, -0.6f, 1.0f, 0.0f, 0.0f, 1.0f,

         0.25f,  0.775f,  0.05f, 0.0f, 0.0f, 1.0f, 1.0f, // chimney     /* (0.35f,  0.725f) (0.25f,  0.775f) */
         0.35f,  0.725f,  0.05f, 0.0f, 0.0f, 1.0f, 1.0f,
         0.35f,  0.725f, -0.05f, 0.0f, 0.0f, 1.0f, 1.0f,
         0.25f,  0.775f, -0.05f, 0.0f, 0.0f, 1.0f, 1.0f,
         0.25f,  1.000f,  0.05f, 0.0f, 0.0f, 1.0f, 1.0f,
         0.35f,  1.000f,  0.05f, 0.0f, 0.0f, 1.0f, 1.0f,
         0.35f,  1.000f, -0.05f, 0.0f, 0.0f, 1.0f, 1.0f,
         0.25f,  1.000f, -0.05f, 0.0f, 0.0f, 1.0f, 1.0f,
    };

    return MakeBuffer(GL_ARRAY_BUFFER, vertices, sizeof(vertices));
};

GLuint BonusTask::InitializeElementBuffer()
{
    static const GLushort elements[] =  {
        0, 4, 1, 5, // base
        2, 6,
        3, 7,
        0, 4,

        8, 12, 9, // roof
        13,
        10,
        11,
        8,
        12,

        14, 18, 15, //chimney
        19,
        16,
        20,
        17,
        21,
        14,
        18
    };

    return MakeBuffer(GL_ELEMENT_ARRAY_BUFFER, elements, sizeof(elements));
}

void BonusTask::DrawTask()
{
    SetRotationMatrix();
    glBindVertexArray(m_uiVertexArrayHandle);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_uiElementsBuffer);
    glDrawElements(GL_QUAD_STRIP, 10, GL_UNSIGNED_SHORT, (void*) 0); // base
    glDrawElements(GL_TRIANGLE_STRIP, 8, GL_UNSIGNED_SHORT, (void*) (10 * sizeof(GLushort))); // roof
    glDrawElements(GL_TRIANGLE_STRIP, 10, GL_UNSIGNED_SHORT, (void*) (18 * sizeof(GLushort))); // chimney
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glBindVertexArray(0);
}

const char* BonusTask::ToString()
{
    return "Bonus Task";
}
