#pragma once

#include "pgr.h"

/// Small spinAngle that is used to increment the house rotation spinAngle (spinAngle variable).
const float SMALL_ANGLE = -6.28f / 100;  // minus -> CW rotation

typedef std::vector<GLuint> ShaderVector, *ShaderVectorPtr;

class ITask
{
public:
    virtual void Draw() = 0;
    virtual void Initialize() = 0;
    virtual void InitializeProgram() = 0;
    virtual void Update() = 0;
    virtual const char* ToString() = 0;
};

class TaskBase : public ITask
{
public:
    TaskBase(void);
    virtual ~TaskBase(void);

    virtual void InitializeProgram();
    virtual void Draw();
    virtual void Update();

protected:
    virtual void ConfigureShaders(ShaderVectorPtr shaders) = 0;
    virtual void DrawTask() = 0;

    GLuint m_uiVertexArrayHandle;
    float m_fSpinAngle;
    GLuint m_uiProgramHandle;
    GLint m_iPositionLocation;
    GLint m_iSpinMatrixLocation;

    GLuint MakeBuffer(GLenum target, const void* bufferData, GLsizei bufferSize);
    GLuint MakeSingleVertexArray();
    void AssignBufferToAttribute(GLuint vao, GLuint location, GLint size, GLenum target, GLuint vertexBuffer, GLsizei stride, GLuint offset);
    void SetRotationMatrix();

    void CheckOpenGl();
};

class OpenGlException
{
public:
    OpenGlException(GLenum error) : m_glError(error) {}

    GLenum GetGlError() const { return m_glError; }

private:
    GLenum m_glError;
};
