//
//  TrackViewCell.m
//  Banda Beat
//
//  Created by Iker Mendilibar on 05/11/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "TrackViewCell.h"
#import "Track.h"
#import "UIImageView+WebCache.h"
#import <QuartzCore/QuartzCore.h>

@implementation TrackViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

-(void)setTrack:(Track*)track
{
    self.titleLabel.text = track.titulo;
    self.groupLabel.text = track.grupo;
    self.durationLabel.text = track.duration;
    
    [self.imageView setImageWithURL:[NSURL URLWithString:track.imageIphone]
                   placeholderImage:[UIImage imageNamed:@"placeholder"] options:SDWebImageProgressiveDownload success:^(UIImage *image, BOOL cached) {
                       self.imageView.image = [self borderedImage:image];
                   } failure:nil];
        
    if ([track.favorite compare:[NSNumber numberWithBool:YES]])
        self.favoriteButton.selected = NO;
    else
        self.favoriteButton.selected = YES;
    
}


- (UIImage *)borderedImage:(UIImage *)image {

    UIGraphicsBeginImageContextWithOptions(image.size, YES, image.scale);
    [image drawAtPoint:CGPointZero];
    [[UIColor whiteColor] set];
    CGRect rect = (CGRect){CGPointZero, image.size};
    const CGFloat frameWidth = 2;
    rect = CGRectInset(rect, frameWidth / 2.0f, frameWidth / 2.0f);
    UIBezierPath *path = [UIBezierPath bezierPathWithRect:rect];
    path.lineWidth = frameWidth;
    [path stroke];
    
    [[UIColor blackColor] set];
    CGRect rect2 = (CGRect){CGPointZero, CGSizeMake(image.size.width, image.size.height)};
    const CGFloat frameWidth2 = 1;
    rect2 = CGRectInset(rect2, frameWidth2 / 2.0f, frameWidth2 / 2.0f);
    UIBezierPath *path2 = [UIBezierPath bezierPathWithRect:rect2];
    path2.lineWidth = frameWidth2;
    [path2 stroke];
    
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    
    return newImage;
}


- (void)setSelected: (BOOL)selected animated: (BOOL)animated
{
    // don't select
    //[super setSelected:selected animated:animated];
}

@end
