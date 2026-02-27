package br.com.cryptoexchange.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.Interceptor;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class NetworkModule_ProvideCmcApiKeyInterceptorFactory implements Factory<Interceptor> {
  @Override
  public Interceptor get() {
    return provideCmcApiKeyInterceptor();
  }

  public static NetworkModule_ProvideCmcApiKeyInterceptorFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static Interceptor provideCmcApiKeyInterceptor() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideCmcApiKeyInterceptor());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideCmcApiKeyInterceptorFactory INSTANCE = new NetworkModule_ProvideCmcApiKeyInterceptorFactory();
  }
}
