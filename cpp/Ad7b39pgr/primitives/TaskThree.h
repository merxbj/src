#pragma once
#include "advancedtask.h"
class TaskThree : public AdvancedTask
{
public:
    TaskThree(void);
    virtual ~TaskThree(void);

    virtual void Initialize();
    virtual const char* ToString();

protected:
    virtual void DrawTask();

private:
    GLuint InitializeVertexBufferHouseWithColors();
    GLuint InitializeVertexArrayHouse(GLuint vertexBuffer);

    GLuint m_uiVertexColorArrayHandle;
};

