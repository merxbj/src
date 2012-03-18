#pragma once
#include "advancedtask.h"
class TaskTwo : public AdvancedTask
{
public:
    TaskTwo(void);
    virtual ~TaskTwo(void);

    virtual void Initialize();
    virtual const char* ToString();

protected:
    virtual void DrawTask();

private:
    GLuint InitializeVertexBufferHouse();
    GLuint InitializeVertexArrayHouse(GLuint vertexBuffer);
    GLuint InitializeVertexBufferHouseColors();

    GLuint m_uiVertexColorArrayHandle;
};

