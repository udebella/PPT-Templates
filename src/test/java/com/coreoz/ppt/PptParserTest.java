package com.coreoz.ppt;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Optional;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.junit.Test;

public class PptParserTest {

	// tests for parse
	
	@Test
	public void when_no_variable_should_return_absent() {
		assertThat(PptParser.parse("test")).isEmpty();
	}
	
	@Test
	public void when_simple_variable_should_return_variable_name() {
		Optional<PptVariable> variable = PptParser.parse("$/variable/");
		
		assertThat(variable).isPresent();
		assertThat(variable.get().getName()).isEqualTo("variable");
		assertThat(variable.get().getArg1()).isNull();
	}
	
	@Test
	public void when_variable_with_argument_should_return_variable_name_and_its_argument() {
		Optional<PptVariable> variable = PptParser.parse("$/variable:'arg value'/");
		
		assertThat(variable).isPresent();
		assertThat(variable.get().getName()).isEqualTo("variable");
		assertThat(variable.get().getArg1()).isEqualTo("arg value");
	}
	
	// tests for replaceTextVariable
	
	@Test
	public void no_variable_content_should_be_untouched() throws IOException {
		try(XMLSlideShow ppt = new XMLSlideShow(PptParserTest.class.getResourceAsStream("/parser/simple_multi_lines.pptx"))) {
			XSLFTextParagraph paragraph = firstParagraph(ppt);
			
			PptParser.replaceTextVariable(paragraph, new PptMapper());
			
			assertThat(paragraph.getText()).isEqualTo("Text on multiple text runs");
		}
	}
	
	@Test
	public void variable_content_with_no_replacement_should_be_untouched() throws IOException {
		try(XMLSlideShow ppt = new XMLSlideShow(PptParserTest.class.getResourceAsStream("/parser/simple_varName_variable.pptx"))) {
			XSLFTextParagraph paragraph = firstParagraph(ppt);
			
			PptParser.replaceTextVariable(paragraph, new PptMapper());
			
			assertThat(paragraph.getText()).isEqualTo("Text with a var: $/varName/");
		}
	}
	
	@Test
	public void variable_content_with_replacement_should_changed() throws IOException {
		try(XMLSlideShow ppt = new XMLSlideShow(PptParserTest.class.getResourceAsStream("/parser/simple_varName_variable.pptx"))) {
			XSLFTextParagraph paragraph = firstParagraph(ppt);
			
			PptParser.replaceTextVariable(paragraph, new PptMapper().text("varName", "replacement"));
			
			assertThat(paragraph.getText()).isEqualTo("Text with a var: replacement");
		}
	}
	
	private XSLFTextParagraph firstParagraph(XMLSlideShow ppt) {
		return ((XSLFTextShape) ppt.getSlides().get(0).getShapes().get(0)).getTextParagraphs().get(0);
	}
	
}
