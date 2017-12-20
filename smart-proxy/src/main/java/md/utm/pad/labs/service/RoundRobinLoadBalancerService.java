package md.utm.pad.labs.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by anrosca on Dec, 2017
 */
@Service
public class RoundRobinLoadBalancerService implements LoadBalancerService {
    private static final Logger LOGGER = Logger.getLogger(RoundRobinLoadBalancerService.class);
    private static final AtomicLong counter = new AtomicLong();

    private List<URI> servingNodes;

    @Autowired
    public RoundRobinLoadBalancerService(List<URI> servingNodes) {
        this.servingNodes = servingNodes;
    }

    @Override
    public URI getServingNode() {
        long counterValue = counter.incrementAndGet();
        int nodeIndex = Long.valueOf(counterValue % servingNodes.size()).intValue();
        return servingNodes.get(nodeIndex);
    }
}
