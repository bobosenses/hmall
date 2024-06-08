package com.hmalll.gateway.routters;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @Author： liwb
 * @Date： 2024/6/9 0:08
 * @Describe：
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicRouteLoader {

    private final NacosConfigManager nacosConfigManager;
    private final RouteDefinitionWriter writer;

    private final String dataId = "gateway-routes.json";
    private final String groupId = "DEFAULT_GROUP";
    private final Set<String> routeIds = new HashSet<>();
    @PostConstruct
    public void loadRouteConfigListener() throws NacosException {
        //项目启动时 拉取一次配置，并添加监听器
        String configInfo = nacosConfigManager.getConfigService().getConfigAndSignListener(dataId, groupId, 5000, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                //监听到配置，更新路由表
                updateConfigInfo(configInfo);
            }
        });
        //第一次取到配置，更新路由表
        updateConfigInfo(configInfo);

    }

    public void updateConfigInfo(String configInfo) {
        log.debug("监听到路由信息: {}", configInfo);
        //1.解析配置信息，转为RouteDefinition
        List<RouteDefinition> routeDefinitions = JSONUtil.toList(configInfo, RouteDefinition.class);
        //2.删除旧的路由表
        for (String routeId : routeIds) {
            writer.delete(Mono.just(routeId)).subscribe();
        }
        routeIds.clear();
        //更新路由表
        for (RouteDefinition routeDefinition : routeDefinitions) {
            // 更新路由表
            writer.save(Mono.just(routeDefinition)).subscribe();
            //记录id 便于下次删除
            routeIds.add(routeDefinition.getId());
        }
    }
}
