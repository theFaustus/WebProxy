package md.utm.pad.labs.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by anrosca on Dec, 2017
 */
@Service
public class RoundRobinLoadBalancerService implements LoadBalancerService, Runnable {
    private static final Logger LOGGER = Logger.getLogger(RoundRobinLoadBalancerService.class);
    private static final AtomicLong counter = new AtomicLong();
    private static final int WAREHOUSE_CRASH_CHECK_INTERVAL = 5;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private List<URI> servingNodes;
    private final List<URI> initialServingNodes;

    private final Lock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        executorService.scheduleAtFixedRate(this, WAREHOUSE_CRASH_CHECK_INTERVAL,
                WAREHOUSE_CRASH_CHECK_INTERVAL, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdownNow();
    }

    @Autowired
    public RoundRobinLoadBalancerService(List<URI> servingNodes) {
        this.servingNodes = servingNodes;
        this.initialServingNodes = servingNodes;
    }

    @Override
    public URI getServingNode() {
        try {
            lock.lock();
            long counterValue = counter.incrementAndGet();
            int nodeIndex = Long.valueOf(counterValue % servingNodes.size()).intValue();
            return servingNodes.get(nodeIndex);
        } finally {
            lock.unlock();
        }
    }

    public void run() {
        List<URI> workingNodes = new ArrayList<>();
        LOGGER.info("Checking the data warehouses.");
        for (URI uri : initialServingNodes) {
            if (isNodeUp(uri)) {
                workingNodes.add(uri);
                LOGGER.info("Node: " + uri + " is up.");
            }
        }
        replaceServingNodes(workingNodes);
    }

    private void replaceServingNodes(List<URI> workingNodes) {
        try {
            lock.lock();
            servingNodes = workingNodes;
        } finally {
            lock.unlock();
        }
    }

    private boolean isNodeUp(URI nodeUri) {
        try {
            RestTemplate template = new RestTemplate();
            template.headForHeaders(nodeUri.toString());
        } catch (Exception e) {
            LOGGER.info("Node: " + nodeUri + " is down.");
            return false;
        }
        return true;
    }
}
