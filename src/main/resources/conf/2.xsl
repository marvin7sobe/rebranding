<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template name="topic_title">
        <xsl:param name="editable">This Tridion will be updated</xsl:param>
        <xsl:param name="value"/>
        <H2>
            <xsl:attribute name="CONTENTEDITABLE">
                <xsl:value-of select="$editable"/>
            </xsl:attribute>
            <xsl:value-of select="$value"/>
        </H2>
    </xsl:template>
</xsl:stylesheet>