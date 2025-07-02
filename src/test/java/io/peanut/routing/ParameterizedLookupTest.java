package io.peanut.routing;

import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.Map;

@DisplayNameGeneration(DisplayNameGenerator.Simple.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ParameterizedLookupTest
{
    private static HttpRouter<String> httpRouter;

    @BeforeAll
    public static void setup()
    {
        httpRouter = HttpRouterFactory.create(config -> {
            config.add("/api/v1/user/:userId/profile", "handlerUserProfile");
            config.add("/api/v1/order/:orderId", "handlerOrder");
            config.add("/api/v1/order/:orderId/view", "handlerOrderView");
            config.add("/shop/:category/:itemId", "handlerShopItem");
        });
    }

    @Test
    @Order(1)
    public void test_route_user_profile_with_userId_param()
    {
        RouteResult<String> result = httpRouter.route("/api/v1/user/123/profile");
        Map<String, String> parameters = result.getParameters();

        Assertions.assertNotNull(result);
        Assertions.assertNotSame(Collections.EMPTY_MAP, parameters);
        Assertions.assertEquals("handlerUserProfile", result.getHandler());
        Assertions.assertEquals("123", parameters.get("userId"));
    }

    @Test
    @Order(2)
    public void test_route_order_with_orderId_param()
    {
        RouteResult<String> result = httpRouter.route("/api/v1/order/abc-456");
        Map<String, String> parameters = result.getParameters();

        Assertions.assertNotNull(result);
        Assertions.assertNotSame(Collections.EMPTY_MAP, parameters);
        Assertions.assertEquals("handlerOrder", result.getHandler());
        Assertions.assertEquals("abc-456", parameters.get("orderId"));
    }

    @Test
    @Order(3)
    public void test_route_order_view_with_orderId_param()
    {
        RouteResult<String> result = httpRouter.route("/api/v1/order/abc-456/view");
        Map<String, String> parameters = result.getParameters();

        Assertions.assertNotNull(result);
        Assertions.assertNotSame(Collections.EMPTY_MAP, parameters);
        Assertions.assertEquals("handlerOrderView", result.getHandler());
        Assertions.assertEquals("abc-456", parameters.get("orderId"));
    }

    @Test
    @Order(4)
    public void test_route_shop_item_with_category_and_itemId_params()
    {
        RouteResult<String> result = httpRouter.route("/shop/electronics/789");
        Map<String, String> parameters = result.getParameters();

        Assertions.assertNotNull(result);
        Assertions.assertNotSame(Collections.EMPTY_MAP, parameters);
        Assertions.assertEquals("handlerShopItem", result.getHandler());
        Assertions.assertEquals("electronics", parameters.get("category"));
        Assertions.assertEquals("789", parameters.get("itemId"));
    }
}