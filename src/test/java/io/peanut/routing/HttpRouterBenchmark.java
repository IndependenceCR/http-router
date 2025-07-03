package io.peanut.routing;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 1, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 100, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class HttpRouterBenchmark
{
    private final HttpRouter<NoopJob> httpRouter = HttpRouterFactory.create(config -> {
        // Existing routes
        config.add("/api/v1/internal/game_request", new NoopJob());
        config.add("/api/v1/internal/game_init", new NoopJob());
        config.add("/api/v1/internal/game_closed", new NoopJob());
        config.add("/api/v1/internal/game_request/:ray_id", new NoopJob());
        config.add("/api/v1/internal/game/:game_id/state", new NoopJob());
        config.add("/api/v1/internal/game/:game_id", new NoopJob());

        config.add("/api/v2/external/metrics", new NoopJob());
        config.add("/api/v2/external/readiness", new NoopJob());
        config.add("/api/v2/external/liveness", new NoopJob());

        // Additional varied-depth routes
        config.add("/api/v1/users", new NoopJob());
        config.add("/api/v1/users/:user_id", new NoopJob());
        config.add("/api/v1/users/:user_id/posts", new NoopJob());
        config.add("/api/v1/users/:user_id/posts/view", new NoopJob());
        config.add("/api/v1/users/:user_id/posts/edit", new NoopJob());
        config.add("/api/v1/users/:user_id/posts/:post_id/comments", new NoopJob());
        config.add("/api/v1/users/:user_id/posts/:post_id/comments/:comment_id", new NoopJob());

        config.add("/health", new NoopJob());
        config.add("/health/ready", new NoopJob());
        config.add("/health/live", new NoopJob());

        config.add("/admin", new NoopJob());
        config.add("/admin/stats", new NoopJob());
        config.add("/admin/settings", new NoopJob());
        config.add("/admin/profile/view", new NoopJob());
        config.add("/admin/settings/:section", new NoopJob());

        config.add("/shop", new NoopJob());
        config.add("/shop/items", new NoopJob());
        config.add("/shop/items/:item_id", new NoopJob());
        config.add("/shop/items/:item_id/price", new NoopJob());
        config.add("/shop/items/:item_id/variants/:variant_id", new NoopJob());

        config.add("/files/:file_id", new NoopJob());
        config.add("/files/:file_id/download", new NoopJob());
        config.add("/files/:file_id/preview", new NoopJob());
    });

    @Benchmark
    public final RouteResult<NoopJob> hit_one_depth()
    {
        return httpRouter.route("/health");
    }

    @Benchmark
    public final RouteResult<NoopJob> hit_two_depth()
    {
        return httpRouter.route("/admin/stats");
    }

    @Benchmark
    public final RouteResult<NoopJob> hit_three_depth()
    {
        return httpRouter.route("/admin/profile/view");
    }

    @Benchmark
    public final RouteResult<NoopJob> hit_four_depth()
    {
        return httpRouter.route("/api/v1/internal/game_closed");
    }

    @Benchmark
    public final RouteResult<NoopJob> hit_two_depth_last_parameterized()
    {
        return httpRouter.route("/files/any");
    }

    @Benchmark
    public final RouteResult<NoopJob> hit_three_depth_last_parameterized()
    {
        return httpRouter.route("/shop/items/any");
    }

    @Benchmark
    public final RouteResult<NoopJob> miss_one_depth_last_unknown()
    {
        return httpRouter.route("/missing");
    }

    @Benchmark
    public final RouteResult<NoopJob> miss_two_depth_last_unknown()
    {
        return httpRouter.route("/api/v3");
    }

    @Benchmark
    public final RouteResult<NoopJob> miss_three_depth_last_unknown()
    {
        return httpRouter.route("/api/v2/any");
    }

    public static void main(String[] args) throws IOException
    {
        Main.main(args);
    }

    public static class NoopJob
    {

    }
}
