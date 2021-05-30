package com.github.gclaussn.ssg.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class PageDataCompilerTest {

  private PageDataCompiler compiler;

  @Before
  public void setUp() {
    compiler = new PageDataCompiler();
  }

  @Test
  public void testNormalizeId() {
    assertThat(compiler.normalizeId(""), equalTo(""));
    assertThat(compiler.normalizeId("page-a"), equalTo("pageA"));
    assertThat(compiler.normalizeId("page_b"), equalTo("pageB"));
    assertThat(compiler.normalizeId("page-set1/sub/x_y_z"), equalTo("pageSet1/sub/xYZ"));
  }
}
