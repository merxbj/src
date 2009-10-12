<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" encoding="windows-1250"/>

    <xsl:template match="/">
        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <title>Work summary</title>
            </head>
            <body>
                <h1>Work Summary of Jarda Merxbauer</h1>
                <xsl:for-each select="/WorkSummary/TwoWeeksSummary">
                    <xsl:call-template name="TwoWeeksTables" />
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>

    <xsl:template name="TwoWeeksTables">
        <h2>Two work weeks: <xsl:value-of select="@timeSpanStart" /> - <xsl:value-of select="@timeSpanEnd" /></h2>
        <xsl:for-each select="WeekSummary">
            <xsl:call-template name="OneWeekTable" />
        </xsl:for-each>
        <xsl:call-template name="TwoWeeksSummaryTable" />
    </xsl:template>
    
    <xsl:template name="OneWeekTable">
        <xsl:variable name="WeekStart" select="@weekStart" />
        <p>
            <table summary="Summary of the one week within the given time span." border="1">
                <caption>Week: <xsl:value-of select="@weekStart" /> - <xsl:value-of select="@weekEnd" /></caption>
                <colgroup>
                    <col />
                </colgroup>
                <colgroup>
                    <col />
                    <col />
                    <col />
                    <col />
                    <col />
                </colgroup>
                <colgroup>
                    <col />
                </colgroup>
                <thead>
                    <tr>
                        <th>Task</th>
                        <th>Monday</th>
                        <th>Tuesday</th>
                        <th>Wednesday</th>
                        <th>Thursday</th>
                        <th>Friday</th>
                        <th>Task total</th>
                    </tr>
                </thead>
                <tfoot>
                    <xsl:call-template name="BuildDaysTotals">
                    </xsl:call-template>
                </tfoot>
                <tbody>
                    <xsl:for-each select="/WorkSummary/TaskDefinitions/Task">
                        <xsl:call-template name="BuildTaskSummaryPerDay">
                            <xsl:with-param name="TaskId" select="@id" />
                            <xsl:with-param name="WeekStart" select="$WeekStart" />
                        </xsl:call-template>
                    </xsl:for-each>
                </tbody>
            </table>
        </p>
    </xsl:template>

    <xsl:template name="BuildTaskSummaryPerDay">
        <xsl:param name="TaskId" />
        <xsl:param name="WeekStart" />
        <tr>
            <td><xsl:value-of select="@name" /></td>
            <td>
                <xsl:call-template name="BuildSingleDayTaskCell">
                    <xsl:with-param name="TaskId" select="$TaskId" />
                    <xsl:with-param name="WeekStart" select="$WeekStart" />
                    <xsl:with-param name="DayOfWeek">Monday</xsl:with-param>
                </xsl:call-template>
            </td>
            <td>
                <xsl:call-template name="BuildSingleDayTaskCell">
                    <xsl:with-param name="TaskId" select="$TaskId" />
                    <xsl:with-param name="WeekStart" select="$WeekStart" />
                    <xsl:with-param name="DayOfWeek">Tuesday</xsl:with-param>
                </xsl:call-template>
            </td>
            <td>
                <xsl:call-template name="BuildSingleDayTaskCell">
                    <xsl:with-param name="TaskId" select="$TaskId" />
                    <xsl:with-param name="WeekStart" select="$WeekStart" />
                    <xsl:with-param name="DayOfWeek">Wednesday</xsl:with-param>
                </xsl:call-template>
            </td>
            <td>
                <xsl:call-template name="BuildSingleDayTaskCell">
                    <xsl:with-param name="TaskId" select="$TaskId" />
                    <xsl:with-param name="WeekStart" select="$WeekStart" />
                    <xsl:with-param name="DayOfWeek">Thursday</xsl:with-param>
                </xsl:call-template>
            </td>
            <td>
                <xsl:call-template name="BuildSingleDayTaskCell">
                    <xsl:with-param name="TaskId" select="$TaskId" />
                    <xsl:with-param name="WeekStart" select="$WeekStart" />
                    <xsl:with-param name="DayOfWeek">Friday</xsl:with-param>
                </xsl:call-template>
            </td>
            <td>0</td>
        </tr>
    </xsl:template>

    <xsl:template name="BuildDaysTotals">
        <tr>
            <th>Day total</th>
            <td><xsl:value-of select="sum(.//Day[@dayName='Monday']/Task/@hours)" /></td>
            <td><xsl:value-of select="sum(.//Day[@dayName='Tuesday']/Task/@hours)" /></td>
            <td><xsl:value-of select="sum(.//Day[@dayName='Wednesday']/Task/@hours)" /></td>
            <td><xsl:value-of select="sum(.//Day[@dayName='Thursday']/Task/@hours)" /></td>
            <td><xsl:value-of select="sum(.//Day[@dayName='Friday']/Task/@hours)" /></td>
            <td><xsl:value-of select="sum(.//Day/Task/@hours)" /></td>
        </tr>
    </xsl:template>

    <xsl:template name="BuildSingleDayTaskCell">
        <xsl:param name="TaskId" />
        <xsl:param name="WeekStart" />
        <xsl:param name="DayOfWeek" />
        <xsl:choose>
            <xsl:when test="//Day[../@weekStart=$WeekStart and @dayName=$DayOfWeek]/Task[@id=$TaskId]/@hours">
                <xsl:value-of select="//Day[../@weekStart=$WeekStart and @dayName=$DayOfWeek]/Task[@id=$TaskId]/@hours" />
            </xsl:when>
            <xsl:otherwise>0</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="TwoWeeksSummaryTable">
        <p>
            <table summary="Total Summary of all tasks." border="1">
                <caption>Tasks total</caption>
                <colgroup>
                    <col />
                    <col />
                    <col />
                    <col />
                    <col />
                </colgroup>
                <thead>
                    <tr>
                        <th>Task</th>
                        <th>Estimate</th>
                        <th>Estimate Correction</th>
                        <th>Worked hours</th>
                        <th>Over/Short</th>
                        <th>ETA</th>
                    </tr>
                </thead>
                <tbody>
                    <xsl:for-each select="/WorkSummary/TaskDefinitions/Task">
                        <xsl:call-template name="BuildTaskSummary">
                            <xsl:with-param name="TaskId" select="@id" />
                        </xsl:call-template>
                    </xsl:for-each>
                </tbody>
            </table>
        </p>
    </xsl:template>

    <xsl:template name="BuildTaskSummary">
        <xsl:param name="TaskId" />
        <tr>
            <td><xsl:value-of select="@name" /></td>
            <td><xsl:value-of select="@estimate" /></td>
            <td><xsl:value-of select="@correction" /></td>
            <td><xsl:value-of select="sum(//Task[@id=$TaskId]/@hours)" /></td>
            <td><xsl:value-of select="@estimate - @correction - sum(//task[@id=$TaskId]/@hours)" /></td>
        </tr>
    </xsl:template>
    
</xsl:stylesheet>
