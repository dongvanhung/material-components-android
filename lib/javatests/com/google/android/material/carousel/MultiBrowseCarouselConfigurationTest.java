/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.material.carousel;

import static com.google.android.material.carousel.CarouselHelper.createCarouselWithWidth;
import static com.google.common.truth.Truth.assertThat;

import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import android.view.View;
import android.view.View.MeasureSpec;
import androidx.test.core.app.ApplicationProvider;
import com.google.android.material.carousel.KeylineState.Keyline;
import com.google.common.collect.Iterables;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/** Tests for {@link MultiBrowseCarouselConfiguration}. */
@RunWith(RobolectricTestRunner.class)
public class MultiBrowseCarouselConfigurationTest {

  @Test
  public void testOnFirstItemMeasuredWithMargins_createsKeylineStateWithCorrectItemSize() {
    MultiBrowseCarouselConfiguration config =
        new MultiBrowseCarouselConfiguration(createCarouselWithWidth(2470));
    View view = createViewWithSize(450, 450);

    KeylineState keylineState = config.onFirstChildMeasuredWithMargins(view);
    assertThat(keylineState.getItemSize()).isEqualTo(450F);
  }

  @Test
  public void testItemLargerThanContainer_resizesToFit() {
    MultiBrowseCarouselConfiguration config =
        new MultiBrowseCarouselConfiguration(createCarouselWithWidth(100));
    View view = createViewWithSize(400, 400);

    KeylineState keylineState = config.onFirstChildMeasuredWithMargins(view);
    assertThat(keylineState.getItemSize()).isAtMost(100F);
  }

  @Test
  public void testItemLargerThanContainerSize_defaultsToFullscreen() {
    Carousel carousel = createCarouselWithWidth(100);
    MultiBrowseCarouselConfiguration config = new MultiBrowseCarouselConfiguration(carousel);
    View view = createViewWithSize(400, 400);

    KeylineState keylineState = config.onFirstChildMeasuredWithMargins(view);

    // A fullscreen layout should be [collapsed-expanded-collapsed] where the collapsed items are
    // outside the bounds of the carousel container and the expanded center item takes up the
    // containers full width.
    assertThat(keylineState.getKeylines()).hasSize(3);
    assertThat(keylineState.getKeylines().get(0).locOffset)
        .isLessThan((float) carousel.getContainerPaddingStart());
    assertThat(Iterables.getLast(keylineState.getKeylines()).locOffset)
        .isGreaterThan((float) (carousel.getContainerWidth() - carousel.getContainerPaddingEnd()));
    assertThat(keylineState.getKeylines().get(1).mask).isEqualTo(0F);
  }

  @Test
  public void testKnownArrangement_correctlyCalculatesKeylineLocations() {
    float[] locOffsets = new float[] {-.5F, 225F, 675F, 942F, 1012F, 1040.5F};

    MultiBrowseCarouselConfiguration config =
        new MultiBrowseCarouselConfiguration(createCarouselWithWidth(1040));
    View view = createViewWithSize(450, 450);

    List<Keyline> keylines = config.onFirstChildMeasuredWithMargins(view).getKeylines();
    for (int i = 0; i < keylines.size(); i++) {
      assertThat(keylines.get(i).locOffset).isEqualTo(locOffsets[i]);
    }
  }

  private static View createViewWithSize(int width, int height) {
    View view = new View(ApplicationProvider.getApplicationContext());
    view.setLayoutParams(new LayoutParams(width, height));
    view.measure(
        MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    return view;
  }
}
