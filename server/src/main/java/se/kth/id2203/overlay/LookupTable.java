/*
 * The MIT License
 *
 * Copyright 2017 Lars Kroll <lkroll@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package se.kth.id2203.overlay;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.TreeMultimap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.UnmodifiableIterator;
import se.kth.id2203.bootstrapping.NodeAssignment;
import se.kth.id2203.networking.NetAddress;

public class LookupTable implements NodeAssignment {

    private static final long serialVersionUID = -8766981433378303267L;

    private final TreeMultimap<Integer, NetAddress> partitions = TreeMultimap.create();

    public Collection<NetAddress> lookup(String key) {
        //int keyHash = key.hashCode();
        //Integer partition = partitions.keySet().floor(keyHash);
        //if (partition == null) {
        //    partition = partitions.keySet().last();
        //}
        //return partitions.get(partition);
        int keyHash = key.hashCode();

        int partition = keyHash % partitions.size();

        return partitions.get(partition);
    }

    public Collection<NetAddress> getNodes() {
        return partitions.values();
    }

    public Set<NetAddress> getPartition(NetAddress addr) {
        for (Integer key : partitions.keySet()) {
            Set<NetAddress> addresses = partitions.get(key);
            for (NetAddress address : addresses) {
                if(addr.equals(address)){
                    //addresses.remove(address); //Todo: check if you should remove yourself?
                    return addresses;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LookupTable(\n");
        for (Integer key : partitions.keySet()) {
            sb.append(key);
            sb.append(" -> ");
            sb.append(Iterables.toString(partitions.get(key)));
            sb.append("\n");
        }
        sb.append(")");
        return sb.toString();
    }

    static LookupTable generate(ImmutableSet<NetAddress> nodes, int replication_degree) {
        LookupTable lut = new LookupTable();

        UnmodifiableIterator<NetAddress> it = nodes.iterator();
        int i = 0;
        while(it.hasNext()){
            for(int k = 0; k < replication_degree; k++){
                if(it.hasNext()){
                    lut.partitions.put(i, it.next());
                }
            }
            i++;
        }
        return lut;
    }

}
