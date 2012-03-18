#pragma once
#include "taskbase.h"

class TaskOne : public TaskBase
{
public:
    TaskOne(void);
    virtual ~TaskOne(void);

    virtual void Initialize();
    virtual const char* ToString();

protected:
    virtual void ConfigureShaders(ShaderVectorPtr shaders);
    virtual void DrawTask();

private:
    GLuint InitializeVertexBufferHouse();

    std::string m_strVertexShader;
    std::string m_strFragmentShader;
};

