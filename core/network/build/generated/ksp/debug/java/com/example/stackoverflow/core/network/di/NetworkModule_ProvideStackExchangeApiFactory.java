package com.example.stackoverflow.core.network.di;

import com.example.stackoverflow.core.network.remote.StackExchangeApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import retrofit2.Retrofit;

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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class NetworkModule_ProvideStackExchangeApiFactory implements Factory<StackExchangeApi> {
  private final Provider<Retrofit> retrofitProvider;

  private NetworkModule_ProvideStackExchangeApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public StackExchangeApi get() {
    return provideStackExchangeApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideStackExchangeApiFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideStackExchangeApiFactory(retrofitProvider);
  }

  public static StackExchangeApi provideStackExchangeApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideStackExchangeApi(retrofit));
  }
}
