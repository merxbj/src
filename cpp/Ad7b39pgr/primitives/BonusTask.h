#pragma once
#include "advancedtask.h"

class BonusTask : public AdvancedTask
{
public:
    BonusTask(void);
    virtual ~BonusTask(void);

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


