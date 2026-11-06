package com.rsalanga.knowledgebase.seemore

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rsalanga.knowledgebase.seemore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.content.setText("""
            Building high-quality Android applications requires careful attention to architecture, maintainability, and user experience. Modern Android development commonly uses Kotlin, Coroutines, Flow, Jetpack Compose, Room, and Hilt to create scalable and testable applications. As projects grow, establishing clear boundaries between presentation, domain, and data layers becomes increasingly important. Well-defined responsibilities help reduce technical debt, improve testability, and make onboarding new developers easier.

            Performance is another critical aspect of mobile development. Applications should minimize unnecessary recompositions, avoid blocking the main thread, and efficiently manage memory usage. Developers should regularly profile their applications, monitor startup time, and identify potential bottlenecks before they affect end users. Continuous integration, static analysis tools, and automated testing can help catch issues early in the development cycle.

            User interfaces should be responsive, accessible, and adaptable to different screen sizes. Android's support for foldables, tablets, and desktop-class experiences makes adaptive layouts an important consideration. Material Design provides guidance for creating consistent and intuitive user experiences while maintaining visual appeal across devices. Thoughtful interactions, smooth animations, and appropriate feedback contribute significantly to perceived application quality.

            Maintaining code quality over time requires discipline and consistent development practices. Code reviews, lint checks, formatting tools, and architectural guidelines help ensure that new features do not compromise the stability of the application. Investing in a strong foundation early in the project often results in faster development and fewer defects in the long run.
        """.trimIndent())
    }
}
