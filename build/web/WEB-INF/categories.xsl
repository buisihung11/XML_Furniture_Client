<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : categories.xsl
    Created on : October 26, 2020, 5:17 PM
    Author     : Admin
    Description:
        Purpose of transformation follows.
-->


<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:template match="/">
        <categories>
            <xsl:for-each select="//li[@id='120928']/ul/li[string(@id)]">
                <xsl:variable name="parentId" select="@id"/>
                <Category>
                    <name>
                        <xsl:value-of select="a" />
                    </name>
                    <url>
                        <xsl:value-of select="a/@href" />
                    </url>
                    <xsl:for-each select="./ul/li[string(@id)]">
                        <xsl:variable name="subCateId" select="@id"/>
                        <subcategory>
                            <name>
                                <xsl:value-of select="a" />
                            </name>
                            <url>
                                <xsl:value-of select="concat('https://www.onekingslane.com/',a/@href)" />
                            </url>
                        </subcategory>
                    </xsl:for-each>
                </Category>
            </xsl:for-each>
        </categories>
    </xsl:template>
</xsl:stylesheet>

