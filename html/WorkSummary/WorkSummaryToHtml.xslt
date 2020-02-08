<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" encoding="windows-1250"/>

    <xsl:template match="/">
        <html xmlns="http://www.w3.org/1999/xhtml">
        <link rel="stylesheet" href="style.css" />
            <head>
                <title>Work summary</title>
            </head>
            <body>
                <h1>Work Summary of Jarda Merxbauer</h1>
                <xsl:apply-templates />
                <xsl:call-template name="BuildTasksSummary" />
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="WeekSummary">
        <xsl:variable name="WeekStart" select="@weekStart" />
        <p>
            <table summary="Summary of the one week within the given time span.">
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
            <td class="taskTitle"><xsl:value-of select="@name" /></td>
            <td class="number">
                <xsl:call-template name="BuildSingleDayTaskCell">
                    <xsl:with-param name="TaskId" select="$TaskId" />
                    <xsl:with-param name="WeekStart" select="$WeekStart" />
                    <xsl:with-param name="DayOfWeek">Monday</xsl:with-param>
                </xsl:call-template>
            </td>
            <td class="number">
                <xsl:call-template name="BuildSingleDayTaskCell">
                    <xsl:with-param name="TaskId" select="$TaskId" />
                    <xsl:with-param name="WeekStart" select="$WeekStart" />
                    <xsl:with-param name="DayOfWeek">Tuesday</xsl:with-param>
                </xsl:call-template>
            </td>
            <td class="number">
                <xsl:call-template name="BuildSingleDayTaskCell">
                    <xsl:with-param name="TaskId" select="$TaskId" />
                    <xsl:with-param name="WeekStart" select="$WeekStart" />
                    <xsl:with-param name="DayOfWeek">Wednesday</xsl:with-param>
                </xsl:call-template>
            </td>
            <td class="number">
                <xsl:call-template name="BuildSingleDayTaskCell">
                    <xsl:with-param name="TaskId" select="$TaskId" />
                    <xsl:with-param name="WeekStart" select="$WeekStart" />
                    <xsl:with-param name="DayOfWeek">Thursday</xsl:with-param>
                </xsl:call-template>
            </td>
            <td class="number">
                <xsl:call-template name="BuildSingleDayTaskCell">
                    <xsl:with-param name="TaskId" select="$TaskId" />
                    <xsl:with-param name="WeekStart" select="$WeekStart" />
                    <xsl:with-param name="DayOfWeek">Friday</xsl:with-param>
                </xsl:call-template>
            </td>
            <td class="taskTotal number"><xsl:value-of select="sum(//Task[../../@weekStart=$WeekStart and @id=$TaskId]/@hours)" /></td>
        </tr>
    </xsl:template>

    <xsl:template name="BuildDaysTotals">
        <tr class="dayTotal">
            <th>Day total</th>
            <td class="number"><xsl:value-of select="sum(.//Day[@dayName='Monday']/Task/@hours)" /></td>
            <td class="number"><xsl:value-of select="sum(.//Day[@dayName='Tuesday']/Task/@hours)" /></td>
            <td class="number"><xsl:value-of select="sum(.//Day[@dayName='Wednesday']/Task/@hours)" /></td>
            <td class="number"><xsl:value-of select="sum(.//Day[@dayName='Thursday']/Task/@hours)" /></td>
            <td class="number"><xsl:value-of select="sum(.//Day[@dayName='Friday']/Task/@hours)" /></td>
            <td class="grandTotal number"><xsl:value-of select="sum(.//Day/Task/@hours)" /></td>
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
    
    <xsl:template name="BuildTasksSummary">
        <p>
            <table summary="Total Summary of all tasks.">
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
                        <th>Worked before</th>
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
        <xsl:variable name="Estimate">
            <xsl:choose>
                <xsl:when test="@estimate">
                    <xsl:value-of select="@estimate" />
                </xsl:when>
                <xsl:otherwise>0</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="Correction">
            <xsl:choose>
                <xsl:when test="@correction">
                    <xsl:value-of select="@correction" />
                </xsl:when>
                <xsl:otherwise>0</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="WorkedHours" select="sum(//Task[@id=$TaskId]/@hours)" />
        <xsl:variable name="OverShort" select="$Estimate - $Correction - $WorkedHours" />
        <xsl:variable name="Eta">
            <xsl:choose>
                <xsl:when test="@eta">
                    <xsl:value-of select="@eta" />
                </xsl:when>
                <xsl:otherwise>N/A</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <tr>
            <td class="taskTitle"><xsl:value-of select="@name" /></td>
            <td class="number"><xsl:value-of select="$Estimate" /></td>
            <td class="number"><xsl:value-of select="$Correction" /></td>
            <td class="number"><xsl:value-of select="$WorkedHours" /></td>
            <td class="number"><xsl:value-of select="$OverShort" /></td>
            <td class="number"><xsl:value-of select="$Eta" /></td>
        </tr>
    </xsl:template>
    
    <xsl:template match="WorkSchedule">
        <p>
            <table summary="Total Summary of all worked hours.">
                <caption>Hours total</caption>
                <colgroup>
                    <col />
                    <col />
                    <col />
                    <col />
                </colgroup>
                <thead>
                    <tr>
                        <th>Worked Hours</th>
                        <th>Schedulued</th>
                        <th>Worked before</th>
                        <th>Over/Short</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><xsl:value-of select="sum(//Task/@hours)" /></td>
                        <td><xsl:value-of select="ScheduledHours" /></td>
                        <td><xsl:value-of select="WorkedBefore" /></td>
                        <td><xsl:value-of select="sum(//Task/@hours) - ScheduledHours + WorkedBefore" /></td>
                    </tr>
                </tbody>
            </table>
        </p>
    </xsl:template>
    
</xsl:stylesheet>
