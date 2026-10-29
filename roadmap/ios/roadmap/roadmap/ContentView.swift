//
//  ContentView.swift
//  roadmap
//
//  Created by RENZ CARLO SALANGA on 10/28/26.
//

import SwiftUI
import Shared

struct ContentView: View {
    
    private let greetingMessage = Greeting().greet()

    
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text(greetingMessage)
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
