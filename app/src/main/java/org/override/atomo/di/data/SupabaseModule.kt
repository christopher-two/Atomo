package org.override.atomo.di.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import org.koin.dsl.module

val SupabaseModule = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = "https://gqlhnmmofiwybodymufy.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdxbGhubW1vZml3eWJvZHltdWZ5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjczMjcyNjgsImV4cCI6MjA4MjkwMzI2OH0.GcPokx-Oqiora6huM8WHu0nKwB_MLvEK5Zc7_-JHJtc"
        ) {
            install(Postgrest)
            install(Realtime)
            install(Storage)
            install(Auth)
        }
    }
}