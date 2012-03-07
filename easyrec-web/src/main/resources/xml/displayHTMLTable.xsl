<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : displayHTMLTable.xsl
    Created on : 24. Februar 2009, 14:24
    Author     : szavrel
    Description:
    Purpose of transformation follows.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
    <xsl:output method="html"/>
    <xsl:template match="verbose">
        <table width="100%">
            <xsl:for-each select="*/*">
                <tr>
                    <xsl:if test="position() mod 2 = 0">
                        <xsl:attribute name="style">background-color:#eeeeee</xsl:attribute>
                    </xsl:if>

                    <xsl:attribute name="class">
                        <xsl:value-of select="name()"/>
                    </xsl:attribute>

                    <td>
                        <xsl:value-of select="name()"/>
                    </td>
                    <td align="right">
                        <xsl:choose>
                            <xsl:when test="name() = 'enddate' or name() = 'startdate'">
                                <xsl:value-of select="substring-before(substring-before(.,'.'), 'T')"/>
				<xsl:text> </xsl:text>
                                <xsl:value-of select="substring-after(substring-before(.,'.'), 'T')"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="."/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                </tr>
            </xsl:for-each>
        </table>
    </xsl:template>

    <xsl:template match="compact">
        <xsl:for-each select="*/*">
            <xsl:value-of select="name()"/>:
            <xsl:value-of select="."/>
            <br></br>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>