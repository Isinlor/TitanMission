#=
Hohmann transfer:
- Julia version: 1.1.1
- Source: https://en.wikipedia.org/wiki/Hohmann_transfer_orbit
=#

r2 = 1.504e12
r1 = 1.52e11
mu = 1.327124e20

Δv1 = sqrt(mu / r1) * (sqrt((2 * r2) / (r1 + r2)) - 1)
Δv2 = sqrt(mu / r2) * (1 - sqrt((2 * r1) / (r1 + r2)))
Δvt = Δv1 + Δv2

println("Δv 1: ", Δv1)
println("Δv 2: ", Δv2)
println("Δv total: ", Δvt)

t = pi * sqrt(((r1 + r2)^3) / (8 * mu))
println("Time: ", t / 60 / 60 / 24 / 365, " years")
α = pi * (1 - ((1 / (2 * sqrt(2)) * sqrt(((r1 / r2) + 1)^3))))
println("Starting angle: ", α * (180 / pi), " degrees")
