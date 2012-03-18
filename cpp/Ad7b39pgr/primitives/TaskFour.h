#pragma once
#include "advancedtask.h"

class TaskFour : public AdvancedTask
{
public:
    TaskFour(void);
    virtual ~TaskFour(void);

    virtual void Initialize();
    virtual const char* ToString();

protected:
    virtual void DrawTask();

private:
    GLuint InitializeVertexBufferHouseWithColors();
    GLuint InitializeElementBuffer();
    GLuint InitializeVertexArrayHouse(GLuint vertexBuffer);

    GLuint m_uiVertexColorArrayHandle;
    GLuint m_uiElementsBuffer;
};

