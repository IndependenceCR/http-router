package io.peanut.routing;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 1, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Group)
public class HttpRouterBenchmark
{
    private final HttpRouter<NoopJob> httpRouter = HttpRouterFactory.create(config -> {
        // API routes
        config.add("/api/v1/internal/game_request", new NoopJob());
        config.add("/api/v1/internal/game_init", new NoopJob());
        config.add("/api/v1/internal/game_closed", new NoopJob());
        config.add("/api/v1/internal/game_request/:ray_id", new NoopJob());
        config.add("/api/v1/internal/game/:game_id/state", new NoopJob());
        config.add("/api/v1/internal/game/:game_id", new NoopJob());

        // User management
        config.add("/user/:id", new NoopJob());
        config.add("/user/view", new NoopJob());
        config.add("/user/:id/settings", new NoopJob());
        config.add("/user/:id/friends", new NoopJob());
        config.add("/user/:id/friends/:friend_id", new NoopJob());

        // Content
        config.add("/blog", new NoopJob());
        config.add("/blog/:id", new NoopJob());
        config.add("/blog/:id/comments", new NoopJob());
        config.add("/blog/:id/:slug", new NoopJob());
        config.add("/blog/:id/:slug/comments", new NoopJob());
    });

    @Benchmark
    @Group("default_group")
    public final RouteResult<NoopJob> single_depth()
    {
        return httpRouter.route("/about");
    }

    @Benchmark
    @Group("default_group")
    public final RouteResult<NoopJob> two_depth()
    {
        return httpRouter.route("/user/view");
    }

    @Benchmark
    @Group("default_group")
    public final RouteResult<NoopJob> four_depth()
    {
        return httpRouter.route("/api/v1/internal/game_closed");
    }

    public static class NoopJob
    {

    }
}
