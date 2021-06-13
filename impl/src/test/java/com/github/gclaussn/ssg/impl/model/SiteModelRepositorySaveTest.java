package com.github.gclaussn.ssg.impl.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.gclaussn.ssg.Page;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.SiteError;
import com.github.gclaussn.ssg.data.PageData;
import com.github.gclaussn.ssg.data.PageDataNodeType;

public class SiteModelRepositorySaveTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder(Paths.get("./target").toFile());
  
  private SiteModelRepository repository;
  
  @Before
  public void setUp() {
    Site site = Site.from(temporaryFolder.getRoot().toPath());
    
    repository = new SiteModelRepository(site);
  }
  
  @Test
  public void shouldSavePage() {
    PageData data = PageData.builder().put("a", "x").put("b", 1).put("c", true).build();

    Optional<SiteError> error = repository.createModelApi().createPageBuilder("index").data(data).saveAndLoad();
    assertThat(error.isEmpty(), is(true));

    Page page = repository.pages.get("index");
    assertThat(page, notNullValue());
    assertThat(page.isSkipped(), is(false));

    assertThat(page.getData().get("a").isNull(), is(false));
    assertThat(page.getData().get("a").is(PageDataNodeType.STRING), is(true));
    assertThat(page.getData().get("a").as(String.class), equalTo("x"));

    assertThat(page.getData().get("b").isNull(), is(false));
    assertThat(page.getData().get("b").is(PageDataNodeType.OTHER), is(true));
    assertThat(page.getData().get("b").as(Integer.class), is(1));

    assertThat(page.getData().get("c").isNull(), is(false));
    assertThat(page.getData().get("c").is(PageDataNodeType.BOOLEAN), is(true));
    assertThat(page.getData().get("c").as(Boolean.class), is(Boolean.TRUE));
  }
}
