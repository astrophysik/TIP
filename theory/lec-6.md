### Напишите вариант программы, для которой анализ открытости-закрытости файлов не показывает корректный результат даже с учётом всех возможных условий в переходах

```
if (cond) {
  flag = 1;  
  open();
} else {
  flag = 0;
}

if (flag == 0) {
  close();
}
```

```
[[entry]] = \p.{closed}
[[cond]] = \p.{closed}
[[flag = 1]] = [flag != 0 -> {closed}, flag = 0 -> {}]
[[open()]] = \p.{open}
[[flag = 0]] = [flag = 0 -> {closed}, flag != 0 -> {}]
[[...]] = \p.((\p.{open})(p) \/ (flag = 0 -> {closed}))

[[assert(flag == 0)]] = ({closed} \/ {open}) = {closed, open} <- Файл может быть закрыт или открыт для flag == 0
close() <- ошибка?
...

```

К правилам с лекции 
```
[[flag = 1]] = JOIN(n)[(flag == 0) -> {}]
[[flag = 0]] = JOIN(n)[(flag == 1) -> {}]
[[assume(flag == 1)]] = JOIN(n)[(flag == 0) -> {}]
[[assume(flag == 0)]] = JOIN(n)[(flag == 1) -> {}]
[[open()]] = \p.{open}
[[close()]] = \p.{closed}
[[entry]] = \p.{closed}
```

Добавим

```
[[open()]] = \p. if JOIN(n)(p) == {} then {} else {open}
[[close()]] = \p. if JOIN(n)(p) == {} then {} else {close}
```

Тогда

```
[[entry]] = \p.{closed}
[[cond]] = \p.{closed}
[[flag = 1]] = [flag != 0 -> {closed}, flag = 0 -> {}]
[[open()]] = \p.if JOIN(n)(p) == {} then {} else {open}
[[flag = 0]] = [flag = 0 -> {closed}, flag != 0 -> {}]
[[...]] = \p.([[open()]](p) \/ (flag = 0 -> {closed}))

[[assert(flag == 0)]] = ({close}) <- Файл точно закрыт
close() <- ошибка!
...
```