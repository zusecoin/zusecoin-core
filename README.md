# zusecoin
Cryptocurrency for Micropayments. AI based contracts. Release 2019

Named after [Konrad Zuse](https://en.wikipedia.org/wiki/Konrad_Zuse), who built the first programmable computer. The Zuse character from Tron Legacy was also named after Konrad Zuse.

## Motivation for ZuseCoin
I needed a cryptocurrency for [OulipoMachine](https://github.com/sisbell/oulipo), which is new kind of web inspired by the original xanalogical design from the Ted Nelson team in the 1970’s and 1980’s. [Ted Nelson](https://en.wikipedia.org/wiki/Ted_Nelson) had the idea of HyperCoin in the early 70’s. It was a concept he baked in early on. If you don’t know about Ted Nelson and his [Xanadu](https://en.wikipedia.org/wiki/Project_Xanadu) project. then google it. You will need to take that red pill to understand what ZuseCoin is all about. 

ZuseCoin is a coin to enable users (like yourself) to be compensated for your user-generated content. If you have a popular blog, you should be rewarded, not advertisers. 

ZuseCoin is about payments. If ZuseCoin becomes a speculative investment, then I will consider it as having failed to meet its goal. Its a far more ambitious project than that.

## Other crypto-currencies

After evaluating a number of cryptocurrencies, they all sadly failed to satisfy my needs for OulipoMachine: 
* reasonable transaction fees
* fast
* secure
* easy to use APIs

Bitcoin transactions are too expensive. When I started working with bitcoin, the transaction fees were 6 cents, now they are over 30 dollars. Even with a lightening network, it requires paying a transaction to setup/close the channels, which won’t work. 

While I really liked the ethereum concept of contracts, again it wasn’t very practical from a cost perspective on setting up payment channels. I quickly rejected other systems for licensing reasons (GPL) or they were closed source.  Most had APIs which were either poorly designed or were not designed to deal with micro-payments or the selling of digital content. 

And finally, I needed  a system that could use TOR without leaking the users IP or identity.  Originally, I wasn’t considering the use of TOR but after seeing where things are headed with the state and mega-evil-companies, it became a hard-requirement for the Oulipo and Zuse systems.

So to meet these criteria, I knew I was going to have to start building a crypto-system from the ground-up.  Fortunately, I had built out the core of a [Warren Abstract Machine](https://en.wikipedia.org/wiki/Warren_Abstract_Machine) (Prolog) a couple of years earlier. There is still a lot of work to finish it up but I decided this would be a good fit for building into ZuseCoin contracts. The security libraries are ported over from OulipoMachine, which in-turn are a major cleanup of the bitid work and extensions I did for Skubit a few years ago.

## Next steps 
I expect to get the first functioning system up by mid 2019. After that I will run it as a test network, until it it is ready for a production launch, however long that takes. 2019 development will also parallel OulipoMachine integration to work out any integration issues and to make sure I haven't missed any critical use-cases.

## Participation
I always welcome participation in my projects. It helps me to move up launch dates. Just ping me if you have something to contribute or have questions.

