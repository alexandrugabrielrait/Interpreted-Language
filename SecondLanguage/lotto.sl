set i 0
goto new
>next
set j i
++ i
>new
set v[i] from random_number.sl
>test
if == i 0
goto next
else
if == v[i] v[j]
goto new
else
if == j 0
goto finish
else
-- j
goto test
end
>finish
if < i 5
goto next
else
>a
println v[0]
println v[1]
println v[2]
println v[3]
println v[4]
println v[5]