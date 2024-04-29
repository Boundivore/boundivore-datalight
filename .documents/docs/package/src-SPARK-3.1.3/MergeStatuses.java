/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.network.shuffle.protocol;

import java.util.Arrays;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.netty.buffer.ByteBuf;
import org.roaringbitmap.RoaringBitmap;

import org.apache.spark.network.protocol.Encoders;

/**
 * Result returned by an ExternalShuffleService to the DAGScheduler. This represents the result
 * of all the remote shuffle block merge operations performed by an ExternalShuffleService
 * for a given shuffle ID. It includes the shuffle ID, an array of bitmaps each representing
 * the set of mapper partition blocks that are merged for a given reducer partition, an array
 * of reducer IDs, and an array of merged shuffle partition sizes. The 3 arrays list information
 * about all the reducer partitions merged by the ExternalShuffleService in the same order.
 *
 * @since 3.1.0
 */
public class MergeStatuses extends BlockTransferMessage {
  /** Shuffle ID **/
  public final int shuffleId;
  /**
   * Array of bitmaps tracking the set of mapper partition blocks merged for each
   * reducer partition
   */
  public final RoaringBitmap[] bitmaps;
  /** Array of reducer IDs **/
  public final int[] reduceIds;
  /**
   * Array of merged shuffle partition block size. Each represents the total size of all
   * merged shuffle partition blocks for one reducer partition.
   * **/
  public final long[] sizes;

  public MergeStatuses(
      int shuffleId,
      RoaringBitmap[] bitmaps,
      int[] reduceIds,
      long[] sizes) {
    this.shuffleId = shuffleId;
    this.bitmaps = bitmaps;
    this.reduceIds = reduceIds;
    this.sizes = sizes;
  }

  @Override
  protected Type type() {
    return Type.MERGE_STATUSES;
  }

  @Override
  public int hashCode() {
    int objectHashCode = Objects.hashCode(shuffleId);
    return (objectHashCode * 41 + Arrays.hashCode(reduceIds) * 41
      + Arrays.hashCode(bitmaps) * 41 + Arrays.hashCode(sizes));
  }

  @Override
  public String toString() {
    // DLC-FIX
    return MoreObjects.toStringHelper(this)
      .add("shuffleId", shuffleId)
      .add("reduceId size", reduceIds.length)
      .toString();
    // DLC-FIX
  }

  @Override
  public boolean equals(Object other) {
    if (other != null && other instanceof MergeStatuses) {
      MergeStatuses o = (MergeStatuses) other;
      return Objects.equal(shuffleId, o.shuffleId)
        && Arrays.equals(bitmaps, o.bitmaps)
        && Arrays.equals(reduceIds, o.reduceIds)
        && Arrays.equals(sizes, o.sizes);
    }
    return false;
  }

  @Override
  public int encodedLength() {
    return 4 // int
      + Encoders.BitmapArrays.encodedLength(bitmaps)
      + Encoders.IntArrays.encodedLength(reduceIds)
      + Encoders.LongArrays.encodedLength(sizes);
  }

  @Override
  public void encode(ByteBuf buf) {
    buf.writeInt(shuffleId);
    Encoders.BitmapArrays.encode(buf, bitmaps);
    Encoders.IntArrays.encode(buf, reduceIds);
    Encoders.LongArrays.encode(buf, sizes);
  }

  public static MergeStatuses decode(ByteBuf buf) {
    int shuffleId = buf.readInt();
    RoaringBitmap[] bitmaps = Encoders.BitmapArrays.decode(buf);
    int[] reduceIds = Encoders.IntArrays.decode(buf);
    long[] sizes = Encoders.LongArrays.decode(buf);
    return new MergeStatuses(shuffleId, bitmaps, reduceIds, sizes);
  }
}
